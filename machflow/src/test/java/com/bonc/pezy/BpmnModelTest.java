package com.bonc.pezy;

/**
 * Created by 冯刚 on 2018/6/12.
 */

import com.bonc.pezy.flow.MachFlow;

public class BpmnModelTest {

    public static void main(String[] args){

       MachFlow mf = new MachFlow();
        /*mf.generateBpmnModel();*/

        System.currentTimeMillis();

        System.out.println("ppppppp"+ System.currentTimeMillis());


        BpmnModelTest bpmnModelTest = new BpmnModelTest();
        /*bpmnModelTest.startActiviti();*/

        /*ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");*/
        /*ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();*/


    }

    /*public void startActiviti(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println("wwwwwww"+ System.currentTimeMillis());
        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();
       *//* TaskService taskService = processEngine.getTaskService();
        ManagementService managementService = processEngine.getManagementService();
        IdentityService identityService = processEngine.getIdentityService();
        HistoryService historyService = processEngine.getHistoryService();
        FormService formService = processEngine.getFormService();*//*

        repositoryService.createDeployment()
                .addClasspathResource("process2.bpmn20.xml")
                .deploy();

        *//*runtimeService.startProcessInstanceByMessage("");*//*
        runtimeService.startProcessInstanceByKey("process2");
    }*/

}
