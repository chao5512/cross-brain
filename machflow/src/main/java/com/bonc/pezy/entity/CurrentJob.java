package com.bonc.pezy.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 冯刚 on 2018/7/30.
 */
@Table(name = "aicjob")
@Entity
public class CurrentJob implements Serializable {

    @Id
    @GenericGenerator(name="cjob_id", strategy="com.bonc.pezy.util.CJobIdGenerator")
    @GeneratedValue(generator="cjob_id")
    @Column(name = "cjobId",unique = true, nullable = false)
    private String cJobId;

    @Column(name = "cName", nullable = false)
    private String cJobName;

    @Column(name = "modelId",nullable = false)
    private String modelId;

    @Column(name = "modelName", nullable = false)
    private String modelName;

    @Column(name = "modelType",nullable = false)
    private short modelType;

    @Column(name = "cjobstatus",nullable = false)
    private String cJobStatus;

    @Column(nullable = false,name = "owner")
    private long owner;//所属用户

    @Column(nullable = false,length = 14,name = "createtime")
    private String createTime;//模型创建时间

    @Column(length = 14,name = "stoptime")
    private String stopTime;//模型创建时间

    @Column(length = 100,name="applicationId")
    private String applicationId;//spark任务id

    @OneToMany(cascade= CascadeType.ALL)
    @JoinColumn(name = "cjobId")
    private List<CurrentNode> currentNodes = new ArrayList<CurrentNode>();

    public String getcJobId() {
        return cJobId;
    }

    public void setcJobId(String cJobId) {
        this.cJobId = cJobId;
    }

    public String getcJobName() {
        return cJobName;
    }

    public void setcJobName(String cJobName) {
        this.cJobName = cJobName;
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

    public String getcJobStatus() {
        return cJobStatus;
    }

    public void setcJobStatus(String cJobStatus) {
        this.cJobStatus = cJobStatus;
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

    public List<CurrentNode> getCurrentNodes() {
        return currentNodes;
    }

    public void setCurrentNodes(List<CurrentNode> currentNodes) {
        this.currentNodes = currentNodes;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
