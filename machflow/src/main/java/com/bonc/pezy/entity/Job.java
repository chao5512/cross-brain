package com.bonc.pezy.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;

import java.util.List;
/**
 * Created by 冯刚 on 2018/7/20.
 */
@Table(name = "aijob")
@Entity
public class Job {

    @Id
    @GenericGenerator(name="job_id", strategy="com.bonc.pezy.util.JobIdGenerator")
    @GeneratedValue(generator="job_id")
    @Column(name = "jobId",unique = true, nullable = false)
    private String jobId;

    @Column(name = "jobName", nullable = false)
    private String jobName;

    @Column(name = "modelId",nullable = false)
    private String modelId;

    @Column(name = "modelName", nullable = false)
    private String modelName;

    @Column(name = "modelType",nullable = false)
    private short modelType;

    @Column(name = "jobstatus",nullable = false)
    private String jobStatus;

    @Column(nullable = false,name = "owner")
    private long owner;//所属用户

    @Column(nullable = false,length = 14,name = "createtime")
    private String createTime;//模型创建时间

    @Column(length = 14,name = "stoptime")
    private String stopTime;//模型创建时间

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "jobId")
    private List<Task> tasks = new ArrayList<Task>();

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public short getModelType() {
        return modelType;
    }

    public void setModelType(short modelType) {
        this.modelType = modelType;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
