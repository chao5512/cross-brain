package com.bonc.pezy.flow;

import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.entity.App;
import com.bonc.pezy.entity.Node;
import com.bonc.pezy.service.NodeService;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 冯刚 on 2018/6/28.
 */
public class DeepLearnFlow {

    public Process generateDLBpmnModel(JSONObject jb, App app, NodeService nodeService){

        GenerateNode generateNode = new GenerateNode();
        Process process=new Process();
        process.setId(app.getProcessId());
        Node node = new Node();
        node.setAppId(app.getAppId());
        node.setNodeName(jb.get("name").toString());
        node.setParam(jb.get("data").toString());
        nodeService.save(node);

        StartEvent startEvent = generateNode.createStartEvent(node.getNodeName(),node.getNodeName());
        ExtensionElement extensionElement= generateNode.createExtensionElement("start", Constants.LISTENER_E);
        List<ExtensionAttribute> list = generateNode.createExtensionAttributes("start",Constants.LR_REGRESSION);
        Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
        mapEA.put("dd",list);
        extensionElement.setAttributes(mapEA);
        List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
        listE.add(extensionElement);
        Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
        mapEE.put("ee",listE);
        startEvent.setExtensionElements(mapEE);
        process.addFlowElement(startEvent);
        return process;
    }
}
