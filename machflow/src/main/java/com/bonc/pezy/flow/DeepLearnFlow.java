package com.bonc.pezy.flow;

import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.entity.Task;
import com.bonc.pezy.service.TaskService;
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

    public Process generateDLBpmnModel(JSONObject jb, Job job, TaskService taskService){

        GenerateNode generateNode = new GenerateNode();
        Process process=new Process();
        process.setId(job.getModelId());
        Task task = new Task();
        task.setJobId(job.getJobId());
        task.setTaskName(jb.get("name").toString());
        task.setParam(jb.get("data").toString());

        taskService.save(task);

        StartEvent startEvent = generateNode.createStartEvent(task.getTaskName(),task.getTaskName());
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
