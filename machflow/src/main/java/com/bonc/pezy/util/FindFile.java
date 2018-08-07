package com.bonc.pezy.util;

/**
 * Created by 冯刚 on 2018/8/6.
 */

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;


public class FindFile {

    public String readFile(String filename,String pathname){

        Properties properties = new Properties();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filename));
            properties.load(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String myString = properties.getProperty(pathname);
        /*File file = new File(filename);
        BufferedReader reader = null;*/
       /* try {
            reader = new BufferedReader(new FileReader(file));
            myString = reader.readLine().toString().trim();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return myString;
    }

    //创建文件目录
    public static boolean mkdir(String dir) throws IOException {
        if (StringUtils.isBlank(dir)) {
            return false;
        }
        Properties properties = new Properties();
        String hdfspath = properties.getProperty("hdfspath");
        dir = hdfspath + dir;
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dir), conf);
        if (!fs.exists(new Path(dir))) {
            fs.mkdirs(new Path(dir));
        }
        fs.close();
        return true;
    }
}
