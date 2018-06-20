package com.bonc.pezy.dataconfig;

/**
 * Created by 冯刚 on 2018/6/19.
 */

public class  DataConfig {


   private static DataConfig dataConfig = null;

    private DataConfig(){

    }

    public static DataConfig getDataConfig(){
        if(null == dataConfig){
            dataConfig = new DataConfig();
        }

        return dataConfig;
    }

    //python微服务链接
    /*@Value("${pyserver.url}")*/
    private  static String url;

    //python微服务端口
    /*@Value("${pyserver.port}")*/
    private static long port;

    private static String path;


    private static String jsondata;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getPort() {
        return port;
    }

    public void setPort(long port) {
        this.port = port;
    }

    public String getJsondata() {
        return jsondata;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setJsondata(String jsondata) {
        this.jsondata = jsondata;
    }
}
