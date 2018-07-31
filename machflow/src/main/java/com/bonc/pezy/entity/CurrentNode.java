package com.bonc.pezy.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 冯刚 on 2018/7/30.
 */
@Table(name = "aicnode")
@Entity
public class CurrentNode implements Serializable,Comparable<CurrentNode>{

    @Id
    @GenericGenerator(name="currentNode_id", strategy="com.bonc.pezy.util.TaskIdGenerator")
    @GeneratedValue(generator="currentNode_id")
    @Column(name = "currentNodeId",unique = true, nullable = false)
    private String currentNodeId;

    @Column(name = "currentNodeName", nullable = false)
    private String currentNodeName;

    @Column(name = "currentJobId")
    private String currentJobId;

    @Column(name = "owner",nullable = false)
    private  long owner;

    //节点出度，下一个节点id
    @Column(name = "outputTaskId")
    private String outputNodeId;

    //节点入度，上一个节点id
    @Column(name = "inputTaskId")
    private String inputNodeId;

    //节点参数
    @Column(name = "param")
    private String param;

    //节点顺序
    @Column(name = "sno")
    private int sno;

    @Column(nullable = false,length = 14,name = "createtime")
    private String createTime;//模型创建时间


    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public void setCurrentNodeId(String currentNodeId) {
        this.currentNodeId = currentNodeId;
    }

    public String getCurrentNodeName() {
        return currentNodeName;
    }

    public void setCurrentNodeName(String currentNodeName) {
        this.currentNodeName = currentNodeName;
    }

    public String getCurrentJobId() {
        return currentJobId;
    }

    public void setCurrentJobId(String currentJobId) {
        this.currentJobId = currentJobId;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
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

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public int compareTo(CurrentNode o) {
        int i = this.sno - o.sno;

        return i;
    }
}
