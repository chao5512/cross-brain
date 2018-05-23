package com.dataset.management.common;

/**
 * API 返回结果类
 */
public class ApiResult {

    private int code;
    private Object data;
    private String message;

    public ApiResult(int code, Object data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public int getcode() {
        return code;
    }

    public void setResult(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
