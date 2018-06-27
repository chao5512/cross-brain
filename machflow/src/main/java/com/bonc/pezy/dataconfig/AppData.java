package com.bonc.pezy.dataconfig;

import java.util.Map;

/**
 * Created by 冯刚 on 2018/6/22.
 */
public class AppData {

    private static AppData appData = null;

    private AppData(){

    }

    public static AppData getAppData(){
        if(null==appData){
            appData = new AppData();
        }

        return appData;
    }

    private String appType;

    private String appName;

/*    private String filepath;*/

    private String processId;

    private Map<String,NodeData> nodeMap;


    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

   /* public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }*/

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Map<String, NodeData> getNodeMap() {
        return nodeMap;
    }

    public void setNodeMap(Map<String, NodeData> nodeMap) {
        this.nodeMap = nodeMap;
    }
}
