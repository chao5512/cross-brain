package com.bonc.pezy.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.dataconfig.ServiceMap;
import com.bonc.pezy.entity.App;
import com.bonc.pezy.flow.DeepLearnFlow;
import com.bonc.pezy.flow.MLFlow;
import com.bonc.pezy.flow.MachFlow;
import com.bonc.pezy.service.AppService;
import com.bonc.pezy.service.NodeService;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by 冯刚 on 2018/6/14.
 */
@CrossOrigin
@Controller
@RequestMapping("/machflow")
public class MachFlowController {


    private final Logger logger = LoggerFactory.getLogger(MachFlowController.class);
    @Autowired
    private AppService appService;

    @Autowired
    private NodeService nodeService;


    private static String filename = "";

    private ServiceMap serviceMap = ServiceMap.getServiceMap();

    @RequestMapping("/analysisCanvas")
    @ResponseBody
    public String analysisCanvas(@RequestParam("jsondata") String jsondata,
                                 @RequestParam("processId") String processId,
                                 @RequestParam("appId") long appid,
                                 @RequestParam("userId") String userid,HttpServletResponse response){

        serviceMap.setAppService(appService);
        serviceMap.setNodeService(nodeService);
        MachFlow mf = new MachFlow();
        System.out.print(jsondata);
        JSONObject jb = JSON.parseObject(jsondata);
        App app = appService.findByUserAndAppId(userid,appid);
        app.setAppName(jb.get("appName").toString());
        app.setProcessId(processId);
        Process process = null;
        if("机器学习模型".equals(jb.get("appType").toString())){
            MLFlow mlFlow = new MLFlow();
            process = mlFlow.generateMLBpmnModel(jb,app,appService);
        }
        if("深度学习模型".equals(jb.get("appType").toString())){
            DeepLearnFlow deepLearnFlow = new DeepLearnFlow();
            process = deepLearnFlow.generateDLBpmnModel(jb,app,nodeService);
        }
        filename = jb.get("processId").toString()+"."+"bpmn20.bpmn";
        if(process != null&&(!"".equals(filename))){
            mf.generateBpmnModel(process,filename);
            return "sucess";
        }else{
            return "error";
        }

    }

    @RequestMapping("/run")
    @ResponseBody
    public String run(@RequestParam("processId") String processId,
                      @RequestParam("appId") int appid,HttpServletResponse respons){
        String aid = String.valueOf(appid);
        MachFlow mf = new MachFlow();
        mf.startActiviti(processId,aid);
        return "sucess";
    }



    @ApiOperation(value = "创建模型",httpMethod = "POST")
    @RequestMapping(value= "/create")
    @ResponseBody
    public boolean create(@RequestParam("modulename") String modulename,
                          @RequestParam("moduletype") short moduletype,
                          @RequestParam("owner") long owner,
                          @RequestParam("createtime") String createtime, HttpServletResponse response){
        App app = new App();
        app.setAppName(modulename);
        app.setAppType(moduletype);
        app.setOwner(owner);
        app.setCreateTime(createtime);
        appService.save(app);
        return true;
    }

    @ApiOperation(value = "模型查询",httpMethod = "POST")
    @RequestMapping(value= "/findByUser")
    @ResponseBody
    public List<App> findModuleByUser(@RequestParam("owner") String owner, HttpServletResponse response){
        return appService.findByUser(owner);
    }


}
