package com.bonc.pezy.flow;

import org.activiti.bpmn.model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 冯刚 on 2018/6/13.
 */
public class GenerateNode {


    /**创建节点*/
    public UserTask createUserTask(String id, String name) {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        return userTask;
    }

    /**创建连线*/
    public SequenceFlow createSequenceFlow(String from, String to, String name, String id, String conditionExpression) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        flow.setName(name);
        flow.setId(id);
        if(StringUtils.isNotEmpty(conditionExpression)){
            flow.setConditionExpression(conditionExpression);
        }
        return flow;
    }

    /**创建开始节点*/
    public StartEvent createStartEvent(String id,String name) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(id);
        startEvent.setName(name);
        return startEvent;
    }

    /**创建结束节点*/
    public EndEvent createEndEvent(String id,String name ) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id);
        endEvent.setName(name);
        return endEvent;
    }

    /**创建节点元素*/
    public List<ExtensionElement> createExtensionElements(Map<String,String> map){

        List<ExtensionElement> list = new ArrayList<ExtensionElement>();

        map.forEach((key, value)-> {
            ExtensionElement extensionElement = new ExtensionElement();
            extensionElement.setId(key);
            extensionElement.setName(value);

            list.add(extensionElement);
        });

        return list;
    }

    public ExtensionElement createExtensionElement(String id,String name){

        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setId(id);
        extensionElement.setName(name);

        return extensionElement;
    }

    /**创建元素属性*/
    public List<ExtensionAttribute> createExtensionAttributes(Map<String,String> map){

        List<ExtensionAttribute> list = new ArrayList<ExtensionAttribute>();
        map.forEach((key, value) -> {
            ExtensionAttribute extensionAttribute = new ExtensionAttribute();
            extensionAttribute.setName(key);
            extensionAttribute.setValue(value);

            list.add(extensionAttribute);
        });

        return list;
    }

    public List<ExtensionAttribute> createExtensionAttributes(String action,String actionclass ){

        List<ExtensionAttribute> list = new ArrayList<ExtensionAttribute>();
        ExtensionAttribute act = new ExtensionAttribute();
        act.setName("event");
        act.setValue(action);
        ExtensionAttribute actclass = new ExtensionAttribute();
        actclass.setName("class");
        actclass.setValue(actionclass);
        list.add(act);
        list.add(actclass);
        return list;
    }
}
