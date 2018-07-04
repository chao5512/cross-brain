package com.bonc.pezy.dataconfig;

import java.util.Map;

/**
 * Created by 冯刚 on 2018/7/2.
 */
public class NodeSet {

    private static NodeSet nodeSet = null;

    private NodeSet(){

    }

    public static NodeSet getNodeSet(){
        if(null==nodeSet){
            nodeSet = new NodeSet();
        }

        return nodeSet;
    }

    public Map<String, NodeData> getNodeMap() {
        return nodeMap;
    }

    public void setNodeMap(Map<String, NodeData> nodeMap) {
        this.nodeMap = nodeMap;
    }

    private Map<String,NodeData> nodeMap;
}
