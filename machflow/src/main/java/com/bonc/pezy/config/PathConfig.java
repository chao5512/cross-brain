package com.bonc.pezy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

/**
 * @ClassName PathConfig
 * @Description 从classpath:conf.properties中读取path信息
 * @Auther: 王培文
 * @Date: 2018/8/15
 * @Version 1.0
 **/

@Component
@ConfigurationProperties(prefix = "")//必须加，即使没有前缀
@PropertySource("classpath:conf.properties")
public class PathConfig {

    private String path;
    private String hdfspath;
    private String hadooppath;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHdfspath() {
        return hdfspath;
    }

    public void setHdfspath(String hdfspath) {
        this.hdfspath = hdfspath;
    }

    public String getHadooppath() {
        return hadooppath;
    }

    public void setHadooppath(String hadooppath) {
        this.hadooppath = hadooppath;
    }
}
