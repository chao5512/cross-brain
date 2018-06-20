package com.bonc.pezy.dataconfig;

/**
 * Created by 冯刚 on 2018/6/19.
 */
public class XmlConfig {


    private static XmlConfig xmlConfig = null;

    private XmlConfig(){

    }

    public static XmlConfig getXmlConfig(){
        if(null == xmlConfig){
            xmlConfig = new XmlConfig();
        }

        return xmlConfig;
    }

    private static String id;
    private static String name;
    private static String processId;
    private static String processName;

    private String listenerType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getListenerType() {
        return listenerType;
    }

    public void setListenerType(String listenerType) {
        this.listenerType = listenerType;
    }
}
