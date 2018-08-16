package com.bonc.pezy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class HdfsConfig {
    @Value("${hadoop.url}")
    private String url;
    @Value("${hadoop.port}")
    private long port;
    @Value("${hadoop.defaultFs}")
    private String defaultFs;
    @Value("${hadoop.host}")
    private String host;

    public String getHdfsUrl() {
        return url;
    }

    public void setHdfsUrl(String hdfsUrl) {
        this.url = hdfsUrl;
    }

    public long getHdfsProt() {
        return port;
    }

    public void setHdfsProt(long hdfsProt) {
        this.port = hdfsProt;
    }

    public String getDefaultFs() {
        return defaultFs;
    }

    public void setDefaultFs(String defaultFs) {
        this.defaultFs = defaultFs;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
