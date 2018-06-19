package com.bonc.pezy.dataconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 冯刚 on 2018/6/19.
 */

@Configuration
public class DataConfig {

    //python微服务链接
    @Value("${pyserver.url}")
    private String url;

    //python微服务端口
    @Value("${pyserver.port}")
    private long port;

    private String path;


    private String jsondata;

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
