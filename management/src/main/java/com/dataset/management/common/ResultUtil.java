package com.dataset.management.common;

public class ResultUtil {
    public static ApiResult success(Object data){
        ApiResult result = new ApiResult(0,data,"OK");
        return result;
    }

    public static ApiResult success(){
        return success(null);
    }

    //当错误时返回的值
    public static ApiResult error(int code ,String msg){
        ApiResult result = new ApiResult(-1,msg);
        return result;
    }
}
