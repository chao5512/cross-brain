package com.bonc.pezy.util;

import com.bonc.pezy.vo.Result;

/**
 * @Author: jaysyd
 * @Date: 2018/4/28 10:21
 * @Description:
 */
public class ResultUtil {
    //当正确时返回的值
    public static Result success(Object data){
        Result result = new Result();
        result.setCode(0);
        result.setMsg("OK");
        result.setData(data);
        return result;
    }

    public static Result success(){
        Result result = new Result();
        result.setCode(0);
        result.setMsg("OK");
        return result;
    }

    //当错误时返回的值
    public static Result error(int code,String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
