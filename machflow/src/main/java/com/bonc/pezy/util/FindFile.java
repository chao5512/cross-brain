package com.bonc.pezy.util;

/**
 * Created by 冯刚 on 2018/8/6.
 */

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;


public class FindFile {

    public String readFile(String filename,String pathname){

        Properties properties = new Properties();

        try {
            InputStream in = this.getClass().getResourceAsStream(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            properties.load(inputStreamReader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String myString = properties.getProperty(pathname);

        return myString;
    }

    //创建文件目录
    public boolean mkdir(String dir) {
        if (StringUtils.isBlank(dir)) {
            return false;
        }
        Properties properties = new Properties();

        try {
            InputStream in = this.getClass().getResourceAsStream("/conf.properties");
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            properties.load(inputStreamReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String hdfspath = properties.getProperty("hdfspath");
        dir = hdfspath + dir;
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(URI.create(dir), conf);
            if (!fs.exists(new Path(dir))) {
                fs.mkdirs(new Path(dir));
            }
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
