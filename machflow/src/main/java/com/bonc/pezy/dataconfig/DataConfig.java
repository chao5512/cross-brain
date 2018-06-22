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


    private static String path;


    private static String jsondata;


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
