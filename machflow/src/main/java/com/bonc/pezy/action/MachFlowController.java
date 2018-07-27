package com.bonc.pezy.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.dataconfig.ServiceMap;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 冯刚 on 2018/6/14.
 */
@CrossOrigin
@Controller
@RequestMapping("/machflow")
@Api(value = "AI模型流程",description = "AI模型流程API")
public class MachFlowController {


    private final Logger logger = LoggerFactory.getLogger(MachFlowController.class);
    @Autowired
    private AppService appService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ModelService modelService;


    private static String filename = "";

    private ServiceMap serviceMap = ServiceMap.getServiceMap();

    @ApiOperation(value = "部署模型",httpMethod = "POST")
    @RequestMapping(value = "/analysisCanvas",method = RequestMethod.POST)
    @ResponseBody
    public String analysisCanvas(@RequestParam("jsondata") String jsondata,
                                 @RequestParam("userId") String userid,
                                 @RequestParam("modelId") String modelId, HttpServletResponse response){

        serviceMap.setAppService(appService);
        serviceMap.setNodeService(nodeService);
        serviceMap.setModelService(modelService);
        serviceMap.setJobService(jobService);
        serviceMap.setTaskService(taskService);
        MachFlow mf = new MachFlow();
        System.out.print(jsondata);
        JSONObject jb = JSON.parseObject(jsondata);
        /*App app = appService.findByUserAndAppId(appid);*/
        Model model = modelService.findById(modelId);
        Job job = new Job();
        job.setJobName("jobName");
        job.setModelId(modelId);
        job.setModelName(model.getModelName());
        job.setModelType(model.getModelType());
        job.setOwner(model.getOwner());
        job.setJobStatus("创建");
        job.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        jobService.create(job);

        Process process = null;
        if(model.getModelType()==1){
            MLFlow mlFlow = new MLFlow();
            process = mlFlow.generateMLBpmnModel(jb,job,jobService);
        }
        if(model.getModelType()==2){
            DeepLearnFlow deepLearnFlow = new DeepLearnFlow();
            process = deepLearnFlow.generateDLBpmnModel(jb,job,taskService);
        }
        filename = jb.get("processId").toString()+"."+"bpmn20.bpmn";
        if(process != null&&(!"".equals(filename))){
            mf.generateBpmnModel(process,filename);
            return "sucess";
        }else{
            return "error";
        }

    }

    @ApiOperation(value = "运行模型",httpMethod = "POST")
    @RequestMapping(value = "/run",method = RequestMethod.POST)
    @ResponseBody
    public String run(@RequestParam("processId") String processId,
                      @RequestParam("appId") int appid,HttpServletResponse respons){
        String aid = String.valueOf(appid);
        MachFlow mf = new MachFlow();
        mf.startActiviti(processId,aid);
        return "sucess";
    }


}
