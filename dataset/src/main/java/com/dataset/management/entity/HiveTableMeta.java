package com.dataset.management.entity;


import org.springframework.stereotype.Component;

import java.util.List;
/**
 * @ClassName HiveTableMeta
 * @Description 表实体类
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
@Component
public class HiveTableMeta {
    //表名
    private String tableName;
    //表注释
    private String tableComment;
    //字段属性集合
    private List<FieldMeta> fields;
    //列分隔符
    private String fieldDelim;
    //行分隔符
    private String lineDelim;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public List<FieldMeta> getFields() {
        return fields;
    }

    public void setFields(List<FieldMeta> fields) {
        this.fields = fields;
    }

    public String getFieldDelim() {
        return fieldDelim;
    }

    public void setFieldDelim(String fieldDelim) {
        this.fieldDelim = fieldDelim;
    }

    public String getLineDelim() {
        return lineDelim;
    }

    public void setLineDelim(String lineDelim) {
        this.lineDelim = lineDelim;
    }
}