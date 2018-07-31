package com.bonc.pezy.vo;

public class JobQuery {

    private Long owner;//所属用户id
    private Short modelType;
    private String createTimeBegin;
    private String createTimeEnd;
    private String modelName;

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Short getModelType() {
        return modelType;
    }

    public void setModelType(Short modelType) {
        this.modelType = modelType;
    }

    public String getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(String createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
