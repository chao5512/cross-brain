package com.bonc.pezy.vo;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Required;

public class JobQuery {

    @NotNull(message = "用户ID不能为空")
    private Long owner;//所属用户id
    private Short modelType;
    @Pattern(regexp = "[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}", message = "起始日期格式不对，需要yyyyMMddHHmmss格式")
    private String createTimeBegin;
    @Pattern(regexp = "[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}", message = "结束日期格式不对，需要yyyyMMddHHmmss格式")
    private String createTimeEnd;
    private String modelName;

    public Long getOwner() {
        return owner;
    }

    @Required
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
