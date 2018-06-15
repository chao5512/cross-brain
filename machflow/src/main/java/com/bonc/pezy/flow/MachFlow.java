package com.bonc.pezy.flow;

import com.bonc.pezy.constants.Constants;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 冯刚 on 2018/5/31.
 */
public class MachFlow {

    public void generateBpmnModel(Map<String,String> map){
        //实例化BpmnModel对象
        BpmnModel bpmnModel=new BpmnModel();
        GenerateNode generateNode = new GenerateNode();
        //开始节点的属性
        StartEvent startEvent = generateNode.createStartEvent(map.get("id"),map.get("name"));
        ExtensionElement extensionElement= generateNode.createExtensionElement("start","activiti:executionListener");

        List<ExtensionAttribute> list = generateNode.createExtensionAttributes("start",Constants.LR_REGRESSION);
        /*ExtensionAttribute extensionAttribute = generateNode.createExtensionAttribute("event", "take");
        ExtensionAttribute extensionAttribute1 = generateNode.createExtensionAttribute("class",);*/
        Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
        mapEA.put("dd",list);
        extensionElement.setAttributes(mapEA);
        List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
        listE.add(extensionElement);
        Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
        mapEE.put("fisrt",listE);
        startEvent.setExtensionElements(mapEE);


        Process process=new Process();

        process.setId("process2");
        process.addFlowElement(startEvent);
        /*extensionElement.setId("start");
        extensionElement.setName("activiti:taskListener");

        ExtensionElement extensionElementprocess = new ExtensionElement();

        *//*process.setExtensionElements();*//*
*/
        bpmnModel.addProcess(process);

        BpmnXMLConverter bpmnXMLConverter=new BpmnXMLConverter();
        byte[] convertToXML = bpmnXMLConverter.convertToXML(bpmnModel);
        String filename = "/Users/fenggang/work/boncprogram/AI/gitdocument/cross-brain/machflow/src/main/resources/"+bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
        File file = new File(filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(convertToXML);
        } catch (Exception e) {

        }
    }

    public void startActiviti() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println("wwwwwww" + System.currentTimeMillis());
        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        repositoryService.createDeployment()
                .addClasspathResource("process2.bpmn20.xml")
                .deploy();

        runtimeService.startProcessInstanceByKey("process2");
    }


    }
