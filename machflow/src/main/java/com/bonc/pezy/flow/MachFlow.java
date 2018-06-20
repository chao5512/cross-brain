package com.bonc.pezy.flow;

import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.dataconfig.DataConfig;
import com.bonc.pezy.dataconfig.XmlConfig;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 冯刚 on 2018/5/31.
 */
@Service
public class MachFlow {

    private XmlConfig xmlConfig = XmlConfig.getXmlConfig();

    private DataConfig dataConfig = DataConfig.getDataConfig();

    private static String filename = "";

    public void generateBpmnModel(String data){
        //实例化BpmnModel对象
        BpmnModel bpmnModel=new BpmnModel();
        GenerateNode generateNode = new GenerateNode();
        Map jb = JSONObject.parseObject(data);
        System.out.println(jb);

        dataConfig.setJsondata(jb.get("data").toString());

        dataConfig.setPath(jb.get("path").toString());

        xmlConfig.setProcessId(jb.get("processid").toString());

        xmlConfig.setId(jb.get("id").toString());

        xmlConfig.setName(jb.get("name").toString());

        filename = jb.get("processid").toString()+"."+"bpmn20.xml";

        //开始节点的属性
        StartEvent startEvent = generateNode.createStartEvent(xmlConfig.getId(),xmlConfig.getName());
        ExtensionElement extensionElement= generateNode.createExtensionElement("start",Constants.LISTENER_E);

        List<ExtensionAttribute> list = generateNode.createExtensionAttributes("start",Constants.LR_REGRESSION);
        Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
        mapEA.put("dd",list);
        extensionElement.setAttributes(mapEA);
        List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
        listE.add(extensionElement);
        Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
        mapEE.put("fisrt",listE);
        startEvent.setExtensionElements(mapEE);


        Process process=new Process();

        process.setId(xmlConfig.getProcessId());
        process.addFlowElement(startEvent);
        /*extensionElement.setId("start");
        extensionElement.setName("activiti:taskListener");

        ExtensionElement extensionElementprocess = new ExtensionElement();

        *//*process.setExtensionElements();*//*
*/
        bpmnModel.addProcess(process);

        BpmnXMLConverter bpmnXMLConverter=new BpmnXMLConverter();
        byte[] convertToXML = bpmnXMLConverter.convertToXML(bpmnModel);
        String filepath = "/Users/fenggang/work/boncprogram/AI/gitdocument/cross-brain/machflow/src/main/resources/"+filename;
        File file = new File(filepath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(convertToXML);
        } catch (Exception e) {

        }
    }

    public void startActiviti() {
        System.out.println("进来没有啊。。。。");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        if(!"".equals(filename)){
            repositoryService.createDeployment()
                    .addClasspathResource(filename)
                    .deploy();
            runtimeService.startProcessInstanceByKey(xmlConfig.getProcessId());
        }

    }

    }
