package com.bonc.pezy.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.dataconfig.ServiceMap;
import com.bonc.pezy.entity.CurrentJob;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.entity.Model;
import com.bonc.pezy.flow.DeepLearnFlow;
import com.bonc.pezy.flow.MLFlow;
import com.bonc.pezy.flow.MachFlow;
import com.bonc.pezy.service.*;
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
import java.util.Date;
import java.util.List;

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

    @Autowired
    private CurrentJobService currentJobService;

    @Autowired
    private CurrentNodeService currentNodeService;


    private static String filename = "";

    private Job jobcom;

    private ServiceMap serviceMap = ServiceMap.getServiceMap();

    @ApiOperation(value = "保存模型",httpMethod = "POST")
    @RequestMapping(value = "/saveModel",method = RequestMethod.POST)
    @ResponseBody
    public CurrentJob saveModel(@RequestParam("jsondata") String jsondata,
                            @RequestParam("userId") String userid,
                            @RequestParam("modelId") String modelId,
                            @RequestParam("jobName") String jobName,HttpServletResponse response){

        Model model = modelService.findById(modelId);
        JSONObject jb = JSON.parseObject(jsondata);
        CurrentJob cjob = currentJobService.findByModelId(modelId);
        if(cjob!=null){

            cjob.setcJobName(jobName);
            cjob.setModelName(model.getModelName());
            cjob.setModelType(model.getModelType());
            cjob.setOwner(model.getOwner());

        }
        CurrentJob currentJob = new CurrentJob();

        currentJob.setcJobName(jobName);
        currentJob.setModelId(modelId);
        currentJob.setModelName(model.getModelName());
        currentJob.setModelType(model.getModelType());
        currentJob.setOwner(model.getOwner());
        currentJob.setcJobStatus("创建");
        currentJob.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        currentJob = currentJobService.save(currentJob);
        return currentJob;


    }



    @ApiOperation(value = "部署模型",httpMethod = "POST")
    @RequestMapping(value = "/analysisCanvas",method = RequestMethod.POST)
    @ResponseBody
    public Job analysisCanvas(@RequestParam("jsondata") String jsondata,
                                 @RequestParam("userId") String userid,
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
        if(list.get(0).getJobStatus()!=1){
            Job job = new Job();
            job.setJobName(jobName);
            job.setModelId(modelId);
            job.setModelName(model.getModelName());
            job.setModelType(model.getModelType());
            job.setOwner(model.getOwner());
            job.setJobStatus(0);
            job.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            jobcom = jobService.create(job);
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
    public String stop(@RequestParam("jobId") String jobId, HttpServletResponse respons){



        return "success";
    }



    /*@ApiOperation(value = "spark任务Id",httpMethod = "POST")
    @RequestMapping(value = "/sparkappid",method = RequestMethod.POST)
    @ResponseBody
    public Job updateapplicationId(@RequestParam() ){

    }*/


}
