package com.dataset.management;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class HdfsTest {

    private static String hdfs = "hdfs://172.16.11.222:9000";
    private static String username = "machen";
    private static String datasetname = "dataset";

    public static FileSystem getFileSystem() {
        //读取配置文件
        Configuration conf = new Configuration();
        // 文件系统
        FileSystem fs = null;
        String hdfsUri = hdfs;
        if(StringUtils.isBlank(hdfsUri)){
            // 返回默认文件系统  如果在 Hadoop集群下运行，使用此种方法可直接获取默认文件系统
            try {
                fs = FileSystem.get(conf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            // 返回指定的文件系统,如果在本地测试，需要使用此种方法获取文件系统
            try {
                URI uri = new URI(hdfsUri.trim());
                fs = FileSystem.get(uri,conf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fs;
    }

    public static void mkdir(String path){
        try {
            FileSystem fs = getFileSystem();
            System.out.println("FilePath="+path);
            // 创建目录
            fs.mkdirs(new Path(path));
            //释放资源
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  boolean existDir(String filePath, boolean create){
        boolean flag = false;

        if (StringUtils.isEmpty(filePath)){
            return flag;
        }
        try{
            Path path = new Path(filePath);
            // FileSystem对象
            FileSystem fs = getFileSystem();

            if (create){
                if (!fs.exists(path)){
                    return false;
                }
            }
            if (fs.isDirectory(path)){
                flag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public static List<String> listAllString (String datasetUserandNameUrl) throws IOException {
        if (StringUtils.isBlank(datasetUserandNameUrl)) {
            return new ArrayList<String>();
        }
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(datasetUserandNameUrl), conf);
        FileStatus[] stats = fs.listStatus(new Path(datasetUserandNameUrl));
        List<String> names = new ArrayList<String>();
        for (int i = 0; i < stats.length; ++i) {
            if (stats[i].isFile()) {
                // regular file
                names.add(stats[i].getPath().toString());
            } else if (stats[i].isDirectory()) {
                // dir
                names.add(stats[i].getPath().toString());
            } else if (stats[i].isSymlink()) {
                // is s symlink in linux
                names.add(stats[i].getPath().toString());
            }
        }

        fs.close();
        return names;
    }

    public static void copyFileToHDFS(String srcFile,String datasetUserandNameUrl)throws Exception{
        FileInputStream fis=new FileInputStream(new File(srcFile));//读取本地文件
        Configuration config=new Configuration();
        FileSystem fs=FileSystem.get(URI.create(hdfs+datasetUserandNameUrl), config);
        OutputStream os=fs.create(new Path(datasetUserandNameUrl));
        //copy
        IOUtils.copyBytes(fis, os, 4096, true);
        System.out.println("拷贝完成...");
        fs.close();
    }

    public static void rmdir(String datasetUserandNameUrl) {
        try {
            // 返回FileSystem对象
            FileSystem fs = getFileSystem();

            String hdfsUri = hdfs;
            if(StringUtils.isNotBlank(hdfsUri)){
                datasetUserandNameUrl = hdfsUri + datasetUserandNameUrl;
            }
            System.out.println("path:"+datasetUserandNameUrl);
            // 删除文件或者文件目录  delete(Path f) 此方法已经弃用
            System.out.println( fs.delete(new Path(datasetUserandNameUrl),true));

            // 释放资源
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean clearFiles(String filePath) throws IOException{
        Path path;
        boolean isDelete =false;
        File files = new File(filePath);
        System.out.println(files.listFiles());
        FileSystem fs = getFileSystem();
        if(StringUtils.isBlank(filePath)){
            return false;
        }
        if(files.isDirectory()){
            String[] filesName = files.list();
            for(int i=0;i<files.length();i++){
               path = new Path(filesName[1]);
                System.out.println(path);
               isDelete = fs.delete(path,true);
            }
        }
        System.out.println(files.isDirectory());
        return isDelete;
    }

    public static void main(String[] args) throws Exception{

        FileSystem fs = getFileSystem();
        System.out.println(fs.getUsed());
        //创建路径
        mkdir("/tmp/user/76");        ///user/hadoop/machen
        //删除路径
//        rmdir("/tmp/user/23");
        //验证是否存在a
//        System.out.println(existDir("/tmp/user/70",false));
//        //上传文件到HDFS
//        copyFileToHDFS("D:\\machen\\kkkk.txt","/user/hadoop/machen/kkkk.txt");
//        //下载文件到本地
//        getFile("/zhaojy/HDFSTest.txt","D:\\HDFSTest.txt");
//        // getFile(HDFSFile,localFile);
//        //删除文件
//        rmdir("/zhaojy2");
        clearFiles("hdfs://172.16.11.222:9000/user/hadoop/ka");
//        //读取文件
//        readFile("/zhaojy/HDFSTest.txt");
    }


}
