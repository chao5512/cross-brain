package com.bonc.pezy.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 冯刚 on 2018/7/20.
 */
@Table(name = "aitask")
@Entity
public class Task implements Serializable,Comparable<Task>{

    @Id
    @GenericGenerator(name="task_id", strategy="com.bonc.pezy.util.TaskIdGenerator")
    @GeneratedValue(generator="task_id")
    @Column(name = "taskId",unique = true, nullable = false)
    private String taskId;

    @Column(name = "taskName", nullable = false)
    private String taskName;

    @Column(name = "jobId")
    private String jobId;

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

    @Column(name = "tasktype")
    private int taskType;

    @Column(name = "taskstatus")
    private int taskStatus;

    //节点顺序
    @Column(name = "sno")
    private int sno;

    @Column(nullable = false,length = 14,name = "createtime")
    private String createTime;//模型创建时间


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
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
    public int compareTo(Task o) {
        int i = this.sno - o.sno;

        return i;
    }
}
