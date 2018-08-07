package com.bonc.pezy.pyapi;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by 冯刚 on 2018/8/3.
 */
public class HttpAPI {

    Map<String,String> map = new HashMap<String,String>();

    public String getHttpResult(Map<String,Object> map, String url){

        String appId = (String)map.get("id");
        /*boolean terminate = true;*/
        OutputStreamWriter out = null;
        StringBuffer buffer = new StringBuffer();
        URL urls = null;
        String str = null;
        try {
            urls = new URL(url);
            HttpURLConnection httpUrlConn = (HttpURLConnection) urls.openConnection();
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            //2.传入参数部分
            // 得到请求的输出流对象
            out = new OutputStreamWriter(httpUrlConn.getOutputStream(),"UTF-8");
            // 把数据写入请求的Body
            out.write("id=" + appId + "&terminate=" + true); //参数形式跟在地址栏的一样
            out.flush();
            out.close();

            int code = httpUrlConn.getResponseCode();
            System.out.println(buffer);

            if (code == 302||code == 200) {
                str = "kill successfully!";
            }else {
                str = "kill unsuccessfully！";
                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                        + httpUrlConn.getResponseCode());
            }
            httpUrlConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return str;

    }
}
