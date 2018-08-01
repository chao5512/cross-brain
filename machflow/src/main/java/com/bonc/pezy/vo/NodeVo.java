package com.bonc.pezy.vo;


import java.io.Serializable;
import javax.validation.constraints.NotNull;

/**
 * Created by 冯刚 on 2018/7/5.
 *
 */

public class NodeVo implements Serializable {

    @NotNull(message = "nodeName不能为空")
    private String nodeName;//前台展示名字
    @NotNull(message = "isComponet不能为null")
    private Boolean isComponet;//是否是组件，有子节点的node不是组件
    @NotNull(message = "className不能为null")
    private String className;//对应的全路径类名
    @NotNull(message = "parentId不能为null")
    private String parentId;//用于树状展示
    @NotNull(message = "type不能为null")
    private Short type;//0:datasource  1:预处理  2:split 3:组件  4:校验
    @NotNull(message = "param不能为null")
    private String param;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Boolean getComponet() {
        return isComponet;
    }

    public void setComponet(Boolean componet) {
        isComponet = componet;
    }
}
