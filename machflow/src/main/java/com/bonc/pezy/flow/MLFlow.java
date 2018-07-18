package com.bonc.pezy.flow;

import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.entity.App;
import com.bonc.pezy.entity.Node;
import com.bonc.pezy.service.AppService;
import com.bonc.pezy.service.NodeService;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;

import java.util.*;

/**
 * Created by 冯刚 on 2018/6/28.
 */
public class MLFlow {

    public Process generateMLBpmnModel(JSONObject jb, App app, AppService appService, NodeService nodeService){

        GenerateNode generateNode = new GenerateNode();
        Process process=new Process();

        List<Node> nodes = new ArrayList<Node>();
        process.setId(app.getProcessId());
        ArrayList<FlowElement> listnode = new ArrayList<FlowElement>();
        FlowElement[] flowElements = new FlowElement[100];
        List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();

        ((Map)jb.get("node")).forEach((key,value)->{

            Node node = new Node();
            /*node.setId(Integer.parseInt(((Map)value).get("sno").toString()));*/
            node.setAppId(app.getAppId());
            node.setNodeName(key.toString());
            node.setInputNodeId(((Map)value).get("InputNodeId").toString());
            node.setOutputNodeId(((Map)value).get("outputNodeId").toString());
            node.setSno(Integer.parseInt(((Map)value).get("sno").toString()));
            node.setParam(((Map)value).get("param").toString());
            nodes.add(node);
        });
        Collections.sort(nodes);
        app.setNodes(nodes);
        appService.save(app);
        for(Node node: nodes){
            /*nodeService.save(node);*/
            if(!"".equals(node.getInputNodeId())&&!"".equals(node.getOutputNodeId())){
                UserTask userTask = generateNode.createUserTask(node.getNodeName(),node.getNodeName());
                ExtensionElement extensionElement= generateNode.createExtensionElement("create", Constants.LISTENER_U);
                List<ExtensionAttribute> list = generateNode.createExtensionAttributes("create",Constants.LR_LISTENER_U);
                Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
                mapEA.put(node.getNodeName(),list);
                extensionElement.setAttributes(mapEA);
                List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
                listE.add(extensionElement);
                Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
                mapEE.put(node.getNodeName(),listE);
                userTask.setExtensionElements(mapEE);
                int i = node.getSno();
                flowElements[i] = userTask;
            }else if(!"".equals(node.getOutputNodeId())){
                StartEvent startEvent = generateNode.createStartEvent(node.getNodeName(),node.getNodeName());
                ExtensionElement extensionElement= generateNode.createExtensionElement("start",Constants.LISTENER_E);
                List<ExtensionAttribute> list = generateNode.createExtensionAttributes("start",Constants.LR_REGRESSION);
                Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
                mapEA.put(node.getNodeName(),list);
                extensionElement.setAttributes(mapEA);
                List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
                listE.add(extensionElement);
                Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
                mapEE.put(node.getNodeName(),listE);
                startEvent.setExtensionElements(mapEE);
                int i = node.getSno();
                flowElements[i] = startEvent;

            }else if(!"".equals(node.getInputNodeId())){
                EndEvent endEvent = generateNode.createEndEvent(node.getNodeName(),node.getNodeName());
                ExtensionElement extensionElement= generateNode.createExtensionElement("end",Constants.LISTENER_E);
                List<ExtensionAttribute> list = generateNode.createExtensionAttributes("end",Constants.LR_REGRESSION);
                Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
                mapEA.put(node.getNodeName(),list);
                extensionElement.setAttributes(mapEA);
                List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
                listE.add(extensionElement);
                Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
                mapEE.put(node.getNodeName(),listE);
                endEvent.setExtensionElements(mapEE);
                int i = node.getSno();
                flowElements[i] = endEvent;
            }
        }

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
            process.addFlowElement(sequenceFlow);
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

        return process;

    }

}
