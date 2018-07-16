package com.bonc.pezy.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

;

/**
 * Created by 冯刚 on 2018/7/5.
 */
@Table(name = "appData")
@Entity
public class App implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "appId",unique = true, nullable = false)
    private int appId;

    @Column(name = "appType",nullable = false)
    private short appType;

    @Column(name = "appName", nullable = false)
    private String appName;

    @Column(name = "processId")
    private String processId;

    @Column(name = "owner",nullable = false)
    private  long owner;

    @Column(nullable = false,length = 14,name = "createtime")
    private String createTime;//模型创建时间

    @Column(nullable = false,length = 14,name = "lastmodifytime")
    private String lastModifyTime;//模型最后修改时间

    @OneToMany
    @JoinColumn(name = "appId")
    private Set<Node> nodes = new HashSet<Node>();

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public short getAppType() {
        return appType;
    }

    public void setAppType(short appType) {
        this.appType = appType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
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
