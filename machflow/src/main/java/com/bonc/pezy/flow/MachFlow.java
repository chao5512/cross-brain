package com.bonc.pezy.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.dataconfig.AppData;
import com.bonc.pezy.dataconfig.DataConfig;
import com.bonc.pezy.dataconfig.NodeData;
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
import java.util.*;

/**
 * Created by 冯刚 on 2018/5/31.
 */
@Service
public class MachFlow {

    private XmlConfig xmlConfig = XmlConfig.getXmlConfig();

    private DataConfig dataConfig = DataConfig.getDataConfig();

    private AppData appData = AppData.getAppData();

    private static String filename = "";

    public void generateBpmnModel(String data){
        //实例化BpmnModel对象
        BpmnModel bpmnModel=new BpmnModel();
        GenerateNode generateNode = new GenerateNode();

        JSONObject jb = JSON.parseObject(data);

        System.out.println(jb);

        //组装app参数数据实体类
        appData.setAppName(jb.get("appName").toString());
        appData.setAppType(jb.get("appType").toString());
        appData.setProcessId(jb.get("processId").toString());

        Map<String,NodeData> map = new HashMap<String, NodeData>();

        //组装各个节点数据
        ((Map)jb.get("node")).forEach((key,value)->{
            NodeData nodeData = new NodeData();
            nodeData.setId(key.toString());
            nodeData.setName(key.toString());
            nodeData.setInputNodeId(((Map)value).get("InputNodeId").toString());
            nodeData.setOutputNodeId(((Map)value).get("outputNodeId").toString());
            nodeData.setPath(((Map)value).get("path").toString());
            nodeData.setParam(((Map)value).get("param").toString());
            nodeData.setSno(((Map)value).get("sno").toString());
            map.put(key.toString(),nodeData);
        });

        if(map.size()>0){
            appData.setNodeMap(map);
        }

        Process process=new Process();
        process.setId(appData.getProcessId());
        ArrayList<FlowElement> listnode = new ArrayList<FlowElement>();
        FlowElement[] flowElements = new FlowElement[100];
        List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();

        appData.getNodeMap().forEach((key,value)->{
            if(!"".equals(value.getInputNodeId())&&!"".equals(value.getOutputNodeId())){
                UserTask userTask = generateNode.createUserTask(key,key);
                ExtensionElement extensionElement= generateNode.createExtensionElement("create",Constants.LISTENER_U);
                List<ExtensionAttribute> list = generateNode.createExtensionAttributes("create",Constants.LR_REGRESSION);
                Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
                mapEA.put(key,list);
                extensionElement.setAttributes(mapEA);
                List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
                listE.add(extensionElement);
                Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
                mapEE.put(key,listE);
                userTask.setExtensionElements(mapEE);
                int i = Integer.parseInt(value.getSno());
                flowElements[i] = userTask;
                /*listnode.add(i,userTask);*/
            }else if(!"".equals(value.getOutputNodeId())){
                StartEvent startEvent = generateNode.createStartEvent(key,key);
                ExtensionElement extensionElement= generateNode.createExtensionElement("start",Constants.LISTENER_E);
                List<ExtensionAttribute> list = generateNode.createExtensionAttributes("start",Constants.LR_REGRESSION);
                Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
                mapEA.put(key,list);
                extensionElement.setAttributes(mapEA);
                List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
                listE.add(extensionElement);
                Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
                mapEE.put(key,listE);
                startEvent.setExtensionElements(mapEE);
                int i = Integer.parseInt(value.getSno());
                flowElements[i] = startEvent;
                /*listnode.add(i,startEvent);*/
                /*listnode.add(startEvent);*/

            }else if(!"".equals(value.getInputNodeId())){
                EndEvent endEvent = generateNode.createEndEvent(key,key);
                ExtensionElement extensionElement= generateNode.createExtensionElement("end",Constants.LISTENER_E);
                List<ExtensionAttribute> list = generateNode.createExtensionAttributes("end",Constants.LR_REGRESSION);
                Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
                mapEA.put(key,list);
                extensionElement.setAttributes(mapEA);
                List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
                listE.add(extensionElement);
                Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
                mapEE.put(key,listE);
                endEvent.setExtensionElements(mapEE);
                int i = Integer.parseInt(value.getSno());
                flowElements[i] = endEvent;
                /*listnode.add(i,endEvent);*/

               /* listnode.add(endEvent);*/
            }
        });

        for(int i =0;i<flowElements.length;i++){
            if(flowElements[i]==null){
                break;
            }
            listnode.add(flowElements[i]);
        }

        for(int i=0;i<listnode.size()-1;i++){

            SequenceFlow sequenceFlow=new SequenceFlow();
            sequenceFlow.setId(listnode.get(i).getId()+listnode.get(i+1).getId());
            sequenceFlow.setName(listnode.get(i).getId()+listnode.get(i+1).getId());
            sequenceFlow.setSourceRef(listnode.get(i).getId());
            sequenceFlow.setTargetRef(listnode.get(i+1).getId());
            sequenceFlows.add(sequenceFlow);
        }

        for (int i=0;i<listnode.size();i++){
            List<SequenceFlow> outsequenceFlows=new ArrayList<SequenceFlow>();
            List<SequenceFlow> insequenceFlows=new ArrayList<SequenceFlow>();
            if(i==0){
                outsequenceFlows.add(sequenceFlows.get(i));
                ((StartEvent)listnode.get(i)).setOutgoingFlows(outsequenceFlows);

            }else if(i==listnode.size()-1){
                insequenceFlows.add(sequenceFlows.get(i-1));
                ((EndEvent)listnode.get(i)).setIncomingFlows(insequenceFlows);
            }else{
                outsequenceFlows.add(sequenceFlows.get(i));
                insequenceFlows.add(sequenceFlows.get(i-1));
                ((UserTask)listnode.get(i)).setOutgoingFlows(outsequenceFlows);
                ((UserTask)listnode.get(i)).setIncomingFlows(insequenceFlows);
            }
            process.addFlowElement(listnode.get(i));
        }


        filename = jb.get("processId").toString()+"."+"bpmn20.xml";
        bpmnModel.addProcess(process);
        BpmnXMLConverter bpmnXMLConverter=new BpmnXMLConverter();
        byte[] convertToXML = bpmnXMLConverter.convertToXML(bpmnModel);
        String filepath = "/Users/fenggang/job/AI/AIStidio/cross-brain/machflow/src/main/resources/"+filename;
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
