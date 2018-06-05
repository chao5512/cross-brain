package com.deepthoughtdata.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Module implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//id唯一标识

    @Column(nullable = false)
    private String modelname;//用户名
    @Column(nullable = false)
    private int modeltype;//密码
    @Column(nullable = false)
    private long owner;//邮箱
    @Column(nullable = false)
    private String createTime;//验证码

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelname() {
        return modelname;
    }

    public void setModelname(String modelname) {
        this.modelname = modelname;
    }

    public int getModeltype() {
        return modeltype;
    }

    public void setModeltype(int modeltype) {
        this.modeltype = modeltype;
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
}
