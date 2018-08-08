package com.bonc.pezy.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.dataconfig.ServiceMap;
import com.bonc.pezy.entity.*;
import com.bonc.pezy.flow.DeepLearnFlow;
import com.bonc.pezy.flow.MLFlow;
import com.bonc.pezy.flow.MachFlow;
import com.bonc.pezy.pyapi.HttpAPI;
import com.bonc.pezy.service.*;
import com.bonc.pezy.util.FindFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 冯刚 on 2018/6/14.
 */
@Controller
@RequestMapping("/machflow")
@Api(value = "AI模型流程",description = "AI模型流程API")
public class MachFlowController {


    private final Logger logger = LoggerFactory.getLogger(MachFlowController.class);

    @Autowired
    private NodeService nodeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ModelService modelService;


    private static String filename = "";

    private Job jobcom;

    private ServiceMap serviceMap = ServiceMap.getServiceMap();

    /*private MachFlowController(){

    }*/

    @ApiOperation(value = "保存模型",httpMethod = "POST")
    @RequestMapping(value = "/analysisCanvas",method = RequestMethod.POST)
    @ResponseBody
    public Job analysisCanvas(@RequestParam("jsondata") String jsondata,
                                 @RequestParam("userId") String userId,
                                 @RequestParam("modelId") String modelId,
                                 @RequestParam("jobName") String jobName,HttpServletResponse response){
        serviceMap.setNodeService(nodeService);
        serviceMap.setModelService(modelService);
        serviceMap.setJobService(jobService);
        serviceMap.setTaskService(taskService);

        MachFlow mf = new MachFlow();
        System.out.print(jsondata);
        JSONObject jb = JSON.parseObject(jsondata);
        Model model = modelService.findById(modelId);

        List<Job> list = jobService.findByModelId(modelId);
        System.out.println(list);
        if(list.isEmpty() || list.get(0).getJobStatus()!=0){
            Job job = new Job();
            job.setJobName(jobName);
            job.setModelId(modelId);
            job.setModelName(model.getModelName());
            job.setModelType(model.getModelType());
            job.setOwner(model.getOwner());
            job.setJobStatus(0);
            job.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            jobcom = jobService.create(job);
            FindFile findFile = new FindFile();
            try {
                findFile.mkdir("/"+userId+"/"+modelId+"/"+jobcom.getJobId()+"/logs");
                findFile.mkdir("/"+userId+"/"+modelId+"/"+jobcom.getJobId()+"/data");
                findFile.mkdir("/"+userId+"/"+modelId+"/"+jobcom.getJobId()+"/model");
                findFile.mkdir("/"+userId+"/"+modelId+"/"+jobcom.getJobId()+"/result");
                findFile.mkdir("/"+userId+"/"+modelId+"/"+jobcom.getJobId()+"/evaluator");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            jobcom = list.get(0);
            jobcom.setJobName(jobName);
        }
        Process process = null;
        if(model.getModelType()==1){
            MLFlow mlFlow = new MLFlow();
            process = mlFlow.generateMLBpmnModel(jb,jobcom,jobService);
        }
        if(model.getModelType()==2){
            DeepLearnFlow deepLearnFlow = new DeepLearnFlow();
            process = deepLearnFlow.generateDLBpmnModel(jb,jobcom,taskService);
        }
        filename = modelId+"."+"bpmn20.bpmn";
        if(process != null&&(!"".equals(filename))){
            mf.generateBpmnModel(process,filename);
        }
        return jobcom;

    }

    @ApiOperation(value = "运行模型",httpMethod = "POST")
    @RequestMapping(value = "/run",method = RequestMethod.POST)
    @ResponseBody
    public String run(@RequestParam("modelId") String modelId,
                      @RequestParam("jobId") String jobId, HttpServletResponse respons){
        MachFlow mf = new MachFlow();
        mf.startActiviti(modelId,jobId);
        return "sucess";
    }

    @ApiOperation(value = "停止模型",httpMethod = "POST")
    @RequestMapping(value = "/stop",method = RequestMethod.POST)
    @ResponseBody
    public String stop(@RequestParam("jobId") String jobId,
                       @RequestParam("applicationId") String applicationId, HttpServletResponse respons){

        FindFile findFile = new FindFile();
        /*String path = findFile.readFile("/Users/fenggang/job/AI/AIStidio/cross-brain/machflow/src/main/resources/conf");*/
        String path = findFile.readFile("conf.properties","path");
        String url = path+"app/kill/";
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("id",applicationId);
        map.put("terminate",true);
        HttpAPI httpAPI = new HttpAPI();
        String msg = httpAPI.getHttpResult(map,url);
        return msg;
    }

    @ApiOperation(value = "加载模型",httpMethod = "POST")
    @RequestMapping(value = "/loadmodel",method = RequestMethod.POST)
    @ResponseBody
    public List<Task> loadmodel(@RequestParam("modelId") String modelId, HttpServletResponse respons){

        List<Job> joblist = jobService.findByModelId(modelId);
        Job job = joblist.get(0);
        List<Task> taskList = job.getTasks();
        for (Task task:taskList) {
            Node node = nodeService.findByClassName(task.getTaskName());
            task.setParam(node.getParam());
        }
        return taskList;
    }





}
