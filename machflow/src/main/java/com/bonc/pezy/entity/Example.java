package com.bonc.pezy.entity;
import javax.persistence.*;
import java.io.Serializable;

@Table(name = "aiexample")
@Entity

public class Example implements Serializable{
    @Id
    @Column(name = "example_id", unique = true, nullable = false)
    private String exampleId;//案例id唯一标识

    @Column(nullable = false,length = 64,name = "example_name")
    private String exampleName;//案例名称
    @Column(nullable = false,name = "example_type")
    private short exampleType;//案例类型
    @Column(nullable = false,name = "model_type")
    private short modelType;//案例所属模型类型
    @Column(nullable = false,name = "description")
    private String description;//模型所属用户
    @Column(nullable = false,name = "doc_name")
    private String docName;//模型所属用户
    @Column(nullable = false,length = 14,name = "create_time")
    private String createTime;//模型创建时间
    @Column(nullable = false,length = 14,name = "lastmodify_time")
    private String lastModifyTime;//模型最后修改时间
    @Column(nullable = false,length = 64,name = "model_id")
    private String modelId;//模型最后修改时间



    public String getExampleId() {
        return exampleId;
    }

    public void setExampleId(String exampleId) {
        this.exampleId = exampleId;
    }

    public String getExampleName() {
        return exampleName;
    }

    public void setExampleName(String exampleName) {
        this.exampleName = exampleName;
    }

    public short getExampleType() {
        return exampleType;
    }

    public void setExampleType(short exampleType) {
        this.exampleType = exampleType;
    }

    public short getModelType() {
        return modelType;
    }

    public void setModelType(short modelType) {
        this.modelType = modelType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
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

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}
