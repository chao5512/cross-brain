package com.dataset.management.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
/**
 * @ClassName FieldMeta
 * @Description 字段实体类
 * @Auther: 王培文
 * @Date: 2018/6/5 
 * @Version 1.0
 **/
@Component
public class FieldMeta {
    //字段名
    private String fieldName;
    //字段类型
    private String fieldType;
    //字段注释
    private String fieldComment;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldComment() {
        return fieldComment;
    }

    public void setFieldComment(String fieldComment) {
        this.fieldComment = fieldComment;
    }
}
