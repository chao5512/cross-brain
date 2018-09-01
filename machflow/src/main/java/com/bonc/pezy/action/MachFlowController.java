package com.bonc.pezy.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.bonc.pezy.dataconfig.ServiceMap;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.entity.Model;
import com.bonc.pezy.entity.Node;
import com.bonc.pezy.entity.Task;
import com.bonc.pezy.flow.DeepLearnFlow;
import com.bonc.pezy.flow.MLFlow;
import com.bonc.pezy.flow.MachFlow;
import com.bonc.pezy.pyapi.HttpAPI;
import com.bonc.pezy.service.JobService;
import com.bonc.pezy.service.ModelService;
import com.bonc.pezy.service.NodeService;
import com.bonc.pezy.service.TaskService;
import com.bonc.pezy.util.FindFile;
import com.bonc.pezy.util.ResultUtil;
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
import java.text.SimpleDateFormat;
import java.util.*;
import com.bonc.pezy.util.ResultUtil;
import com.bonc.pezy.vo.Result;

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


    @ApiOperation(value = "保存模型",httpMethod = "POST")
    @RequestMapping(value = "/analysisCanvas",method = RequestMethod.POST)
    @ResponseBody
    public Job analysisCanvas(@RequestParam("jsondata") String jsondata,
                                 @RequestParam("userId") String userId,
                                 @RequestParam("modelId") String modelId,
                                 @RequestParam("jobName") String jobName,HttpServletResponse response){

        MachFlow mf = new MachFlow();
        System.out.print(jsondata);

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
                findFile.mkdir("/"+userId+"/"+modelId+"/"+jobcom.getJobId()+"/messagedata");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            jobcom = list.get(0);
            jobcom.setJobName(jobName);
        }
        Process process = null;
        if(model.getModelType()==1){
            JSONArray jb = JSONArray.parseArray(jsondata);
            MLFlow mlFlow = new MLFlow();
            process = mlFlow.generateMLBpmnModel(jb,jobcom,jobService);
        }
        if(model.getModelType()==2){
            JSONArray jb = JSONArray.parseArray(jsondata);
            MLFlow mlFlow = new MLFlow();
            process = mlFlow.generateMLBpmnModel(jb,jobcom,jobService);
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
        serviceMap.setNodeService(nodeService);
        serviceMap.setModelService(modelService);
        serviceMap.setJobService(jobService);
        serviceMap.setTaskService(taskService);
        MachFlow mf = new MachFlow();
        mf.startActiviti(modelId,jobId);
        return "sucess";
    }

    @ApiOperation(value = "停止模型",httpMethod = "POST")
    @RequestMapping(value = "/stop",method = RequestMethod.POST)
    @ResponseBody
    public Result stop(@RequestParam("jobId") String jobId,
                       @RequestParam("applicationId") String applicationId, HttpServletResponse respons){

        FindFile findFile = new FindFile();
        String path = findFile.readFile("/conf.properties","path");
        String url = path+"app/kill/";
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("id",applicationId);
        map.put("terminate",true);
        HttpAPI httpAPI = new HttpAPI();
        String msg = httpAPI.getHttpResult(map,url);
        return ResultUtil.success(msg);
    }

    @ApiOperation(value = "加载模型",httpMethod = "POST")
    @RequestMapping(value = "/loadmodel",method = RequestMethod.POST)
    @ResponseBody
    public Result loadmodel(@RequestParam("modelId") String modelId, HttpServletResponse respons){

        List<Job> joblist = jobService.findByModelId(modelId);
        if(joblist.size()==0){
            return ResultUtil.success();
        }
        Job job = joblist.get(0);
        List<Task> taskList = job.getTasks();
        for (Task task:taskList) {
            Node node = nodeService.findByClassName(task.getTaskName());
            task.setParam(node.getParam());
        }
        return ResultUtil.success(job);
    }

}
