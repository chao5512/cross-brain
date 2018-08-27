package com.bonc.pezy.flow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.entity.Task;
import com.bonc.pezy.service.JobService;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 冯刚 on 2018/6/28.
 */
public class MLFlow {

    public Process generateMLBpmnModel(JSONArray jb, Job job, JobService jobService){

        GenerateNode generateNode = new GenerateNode();
        Process process=new Process();

        List<Task> tasks = new ArrayList<Task>();
        process.setId(job.getModelId());
        ArrayList<FlowElement> listnode = new ArrayList<FlowElement>();
        FlowElement[] flowElements = new FlowElement[100];
        List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();

        for(int i=0;i<jb.size();i++){
        //((Map)jb.get("node")).forEach((key,value)->{
            JSONObject json = jb.getJSONObject(i);
            Task task = new Task();
            task.setJobId(job.getJobId());
            task.setOwner(job.getOwner());
            task.setTaskName((String)json.get("taskName"));
            task.setInputNodeId(json.get("inputNodeId").toString());
            task.setOutputNodeId(json.get("outputNodeId").toString());
            task.setSno(Integer.parseInt(json.get("sno").toString()));
            task.setParam(json.get("param").toString());
            task.setTaskType(Integer.parseInt(json.get("type").toString()));
            task.setTaskCnName(json.get("name").toString());
            //task.setTaskName(key.toString());
            //task.setInputNodeId(((Map)value).get("InputNodeId").toString());
            //task.setOutputNodeId(((Map)value).get("outputNodeId").toString());
            //task.setSno(Integer.parseInt(((Map)value).get("sno").toString()));
            //task.setParam(((Map)value).get("param").toString());
            //task.setTaskType(Integer.parseInt(((Map)value).get("type").toString()));
            task.setTaskStatus(0);
            task.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            tasks.add(task);
        };
        Collections.sort(tasks);
        job.setTasks(tasks);
        jobService.save(job);
        for(Task task: tasks){
            /*nodeService.save(node);*/
            if(!"".equals(task.getInputNodeId())&&!"".equals(task.getOutputNodeId())){
                UserTask userTask = generateNode.createUserTask(task.getTaskName(),task.getTaskName());
                ExtensionElement extensionElement= generateNode.createExtensionElement("create", Constants.LISTENER_U);
                List<ExtensionAttribute> list = generateNode.createExtensionAttributes("create",Constants.LR_LISTENER_U);
                Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
                mapEA.put(task.getTaskName(),list);
                extensionElement.setAttributes(mapEA);
                List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
                listE.add(extensionElement);
                Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
                mapEE.put(task.getTaskName(),listE);
                userTask.setExtensionElements(mapEE);
                int i = task.getSno();
                flowElements[i] = userTask;
            }else if(!"".equals(task.getOutputNodeId())){
                StartEvent startEvent = generateNode.createStartEvent(task.getTaskName(),task.getTaskName());
                ExtensionElement extensionElement= generateNode.createExtensionElement("start",Constants.LISTENER_E);
                List<ExtensionAttribute> list = generateNode.createExtensionAttributes("start",Constants.LR_REGRESSION);
                Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
                mapEA.put(task.getTaskName(),list);
                extensionElement.setAttributes(mapEA);
                List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
                listE.add(extensionElement);
                Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
                mapEE.put(task.getTaskName(),listE);
                startEvent.setExtensionElements(mapEE);
                int i = task.getSno();
                flowElements[i] = startEvent;

            }else if(!"".equals(task.getInputNodeId())){
                EndEvent endEvent = generateNode.createEndEvent(task.getTaskName(),task.getTaskName());
                ExtensionElement extensionElement= generateNode.createExtensionElement("end",Constants.LISTENER_E);
                List<ExtensionAttribute> list = generateNode.createExtensionAttributes("end",Constants.LR_REGRESSION);
                Map<String,List<ExtensionAttribute>> mapEA = new HashMap<String, List<ExtensionAttribute>>();
                mapEA.put(task.getTaskName(),list);
                extensionElement.setAttributes(mapEA);
                List<ExtensionElement> listE = new ArrayList<ExtensionElement>();
                listE.add(extensionElement);
                Map<String,List<ExtensionElement>> mapEE = new HashMap<String, List<ExtensionElement>>();
                mapEE.put(task.getTaskName(),listE);
                endEvent.setExtensionElements(mapEE);
                int i = task.getSno();
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
