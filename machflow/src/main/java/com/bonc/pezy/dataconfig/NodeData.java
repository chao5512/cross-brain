package com.bonc.pezy.dataconfig;

/**
 * Created by 冯刚 on 2018/6/22.
 */
public class NodeData {

    /*private static NodeData nodeData = null;

    private NodeData(){

    }


    public static NodeData getNodeData(){
        if(null==nodeData){
            nodeData = new NodeData();
        }

        return nodeData;
    }*/

    //节点id，标识唯一
    private String id;

    //节点名称，可以根据名称判断出节点类型
    private String name;

   /* //节点执行方法路径
    private String path;*/

    //节点出度，下一个节点id
    private String outputNodeId;

    //节点入度，上一个节点id
    private String inputNodeId;

    //节点参数
    private String param;

    //节点顺序
    private String sno;

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

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

   /* public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }*/

    public String getOutputNodeId() {
        return outputNodeId;
    }

    public void setOutputNodeId(String outputNodeId) {
        this.outputNodeId = outputNodeId;
    }

    public String getInputNodeId() {
        return inputNodeId;
    }

    public void setInputNodeId(String inputNodeId) {
        this.inputNodeId = inputNodeId;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }


}
