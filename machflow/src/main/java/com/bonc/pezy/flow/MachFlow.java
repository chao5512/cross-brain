package com.bonc.pezy.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.util.BpmnToXml;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;

/**
 * Created by 冯刚 on 2018/5/31.
 */
public class MachFlow {

    private static String filename = "";

    public void generateBpmnModel(String data){
        System.out.println(data);
        JSONObject jb = JSON.parseObject(data);
        System.out.println(jb);
        Process process = null;
        if("ml".equals(jb.get("appType").toString())){
            MLFlow mlFlow = new MLFlow();
            process = mlFlow.generateMLBpmnModel(jb);
        }
        if("deep".equals(jb.get("appType").toString())){
            DeepLearnFlow deepLearnFlow = new DeepLearnFlow();
            process = deepLearnFlow.generateDLBpmnModel(jb);
        }
        filename = jb.get("processId").toString()+"."+"bpmn20.xml";
        //实例化BpmnModel对象
        BpmnModel bpmnModel=new BpmnModel();
        bpmnModel.addProcess(process);
        BpmnToXml bpmnToXml = new BpmnToXml();
        bpmnToXml.createXMLStream(bpmnModel,filename);

    }

    public void startActiviti(String processId) {
        System.out.println("进来没有啊。。。。");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        if(!"".equals(filename)){
            repositoryService.createDeployment()
                    .addClasspathResource(filename)
                    .deploy();
            runtimeService.startProcessInstanceByKey(processId);
        }

    }

}
