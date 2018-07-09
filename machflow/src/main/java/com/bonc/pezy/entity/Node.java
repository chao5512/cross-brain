package com.bonc.pezy.entity;



import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 冯刚 on 2018/7/5.
 */
@Table(name = "node")
@Entity
public class Node implements Serializable {

    //节点id，标识唯一
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true, nullable = false)
    private int id;

    //模型id,此节点属于哪个模型
    @Column(name = "appId",unique = true, nullable = false)
    private int appId;

    //节点名称，可以根据名称判断出节点类型
    @Column(name = "nodeName")
    private String nodeName;

    //节点出度，下一个节点id
    @Column(name = "outputNodeId")
    private String outputNodeId;

    //节点入度，上一个节点id
    @Column(name = "inputNodeId")
    private String inputNodeId;

    //节点参数
    @Column(name = "param")
    private String param;

    //节点顺序
    @Column(name = "sno")
    private String sno;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

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

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }


}
