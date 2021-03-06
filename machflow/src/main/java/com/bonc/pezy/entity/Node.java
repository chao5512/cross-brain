package com.bonc.pezy.entity;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 冯刚 on 2018/7/5.
 *
 */
@Table(name = "node")
@Entity
public class Node implements Serializable {

    //节点id，标识唯一
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true, nullable = false)
    private long id;

    @Column(name = "name",nullable = false)
    private String name;//前台展示名字

    @Column(name = "isComponet",nullable = false)
    private Boolean isComponet;//是否是组件，有子节点的node不是组件

    @Column(name = "className")
    private String className;//对应的全路径类名

    @Column(name = "parentId",nullable = false)
    private String parentId;//用于树状展示

    @Column(name = "type",nullable = false)
    private Short type;//0:datasource  2:预处理  1:split 3:组件  4:校验

    @Column(name = "param")
    private String param;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Boolean getIsComponet() {
        return isComponet;
    }

    public void setIsComponet(Boolean componet) {
        isComponet = componet;
    }
}
