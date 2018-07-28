package com.bonc.pezy.vo;

/**
 * @Author: jaysyd
 * @Date: 2018/4/28 10:19
 * @Description: 通用ajax返回类
 */
public class Result<T> {

    private int code;//状态码
    private String msg;//信息
    private Object data;//数据

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
