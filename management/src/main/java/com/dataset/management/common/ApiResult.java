package com.dataset.management.common;

/**
 * API 返回结果类
 */
public class ApiResult {

    private boolean result;
    private Object data;
    private String message;

    public ApiResult(boolean result, Object data, String message) {
        this.result = result;
        this.data = data;
        this.message = message;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
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
