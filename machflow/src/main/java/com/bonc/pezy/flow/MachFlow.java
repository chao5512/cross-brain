package com.bonc.pezy.flow;

import com.alibaba.fastjson.JSONObject;
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

    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private RepositoryService repositoryService = processEngine.getRepositoryService();

    public void generateBpmnModel(JSONObject jb){
        /*System.out.println(data);
        JSONObject jb = JSON.parseObject(data);*/
        System.out.println(jb);
        Process process = null;
        if("机器学习模型".equals(jb.get("appType").toString())){
            MLFlow mlFlow = new MLFlow();
            process = mlFlow.generateMLBpmnModel(jb);
        }
        if("深度学习模型".equals(jb.get("appType").toString())){
            DeepLearnFlow deepLearnFlow = new DeepLearnFlow();
            process = deepLearnFlow.generateDLBpmnModel(jb);
        }
        filename = jb.get("processId").toString()+"."+"bpmn20.bpmn";
        //实例化BpmnModel对象
        BpmnModel bpmnModel=new BpmnModel();
        bpmnModel.addProcess(process);
        repositoryService.createDeployment().addBpmnModel(filename,bpmnModel)
                .name("deply").deploy();

    }

    public void startActiviti(String processId) {
        System.out.println("进来没有啊。。。。");

        runtimeService.startProcessInstanceByKey(processId);

        /*if(!"".equals(filename)){
            repositoryService.createDeployment()
                    .addClasspathResource(filename)
                    .deploy();
            runtimeService.startProcessInstanceByKey(processId);
        }else {
            System.out.println("NO task is start");
        }*/

    }

}
