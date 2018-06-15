package com.bonc.pezy.flow;

import com.bonc.pezy.constants.Constants;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;

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

    public void generateBpmnModel(){
        //实例化BpmnModel对象
        BpmnModel bpmnModel=new BpmnModel();
        GenerateNode generateNode = new GenerateNode();
        //开始节点的属性
        StartEvent startEvent = generateNode.createStartEvent("Logicalregression","Logicalregression");
        ExtensionElement extensionElement= generateNode.createExtensionElement("start","activiti:executionListener");

        List<ExtensionAttribute> list = generateNode.createExtensionAttributes("start",Constants.LR_REGRESSION);
        /*ExtensionAttribute extensionAttribute = generateNode.createExtensionAttribute("event", "take");
        ExtensionAttribute extensionAttribute1 = generateNode.createExtensionAttribute("class",);*/
        Map<String,List<ExtensionAttribute>> map = new HashMap<String, List<ExtensionAttribute>>();
        map.put("dd",list);
        extensionElement.setAttributes(map);
        List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
        listE.add(extensionElement);
        Map<String,List<ExtensionElement>> mapE = new HashMap<String, List<ExtensionElement>>();
        mapE.put("fisrt",listE);
        startEvent.setExtensionElements(mapE);


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


}
