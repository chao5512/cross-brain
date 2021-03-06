package com.bonc.pezy.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "aimodel")
@Entity
public class Model implements Serializable{
    @Id
    @GenericGenerator(name="seq_id", strategy="com.bonc.pezy.util.ModelIdGenerator")
    @GeneratedValue(generator="seq_id")
    @Column(name = "modelId", unique = true, nullable = false)
    private String modelId;//模型id唯一标识

    @Column(nullable = false,length = 64,name = "modelName")
    private String modelName;//模型名称
    @Column(nullable = false,name = "modelType")
    private short modelType;//模型类型
    @Column(nullable = false,name = "owner")
    private long owner;//模型所属用户
    @Column(nullable = false,length = 14,name = "createTime")
    private String createTime;//模型创建时间
    @Column(nullable = false,length = 14,name = "lastmodifyTime")
    private String lastModifyTime;//模型最后修改时间

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

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

}
