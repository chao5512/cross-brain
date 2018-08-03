package com.bonc.pezy.flow;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

/**
 * Created by 冯刚 on 2018/5/31.
 */
public class MachFlow {

    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private RepositoryService repositoryService = processEngine.getRepositoryService();
    private ProcessInstance processInstance;

    public void generateBpmnModel(Process process,String filename){

        //实例化BpmnModel对象
        BpmnModel bpmnModel=new BpmnModel();
        bpmnModel.addProcess(process);
        repositoryService.createDeployment().addBpmnModel(filename,bpmnModel)
                .name("deply").deploy();

    }

    public void startActiviti(String modelId,String jobId) {

        processInstance = runtimeService.startProcessInstanceByKey(modelId,jobId);

    }

}
