package com.deepthoughtdata.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Job implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long modelid;//id唯一标识

    private Long jobId;//任务ID

    @Column(nullable = false)
    private String jobname;//任务名称

    @Column(nullable = false)
    private int status;//任务状态

    @Column(nullable = false)
    private long owner;//所属用户
    @Column(nullable = false)
    private String startTime;//开始执行时间
    @Column(nullable = false)
    private String endTime;//开始执行时间

    @Column(nullable = false)
    private String costTime;//开始执行时间

    public Long getModelid() {
        return modelid;
    }

    public void setModelid(Long modelid) {
        this.modelid = modelid;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
