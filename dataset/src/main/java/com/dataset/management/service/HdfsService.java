package com.dataset.management.service;

import com.dataset.management.config.HdfsConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class HdfsService {

    @Autowired
    private HdfsConfig hdfsConfig;

    private  String userName;
    private  String dataSetName;
    private  String hdfsurl;

    private FileSystem getFileSystem() {
        //读取配置文件
        Configuration conf = new Configuration();
        // 文件系统
        FileSystem fs = null;
        hdfsurl = hdfsConfig.getHdfsUrl()+":"+hdfsConfig.getHdfsProt();
        if(StringUtils.isBlank(hdfsurl)){
            // 返回默认文件系统  如果在 Hadoop集群下运行，使用此种方法可直接获取默认文件系统
            try {
                fs = FileSystem.get(conf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            // 返回指定的文件系统,如果在本地测试，需要使用此种方法获取文件系统
            try {
                URI uri = new URI(hdfsurl.trim());
                fs = FileSystem.get(uri,conf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fs;
    }

    public void mkdirHdfsDir(String path){
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

    public void renameDir(String oldPath,String newPath){
        try {
            FileSystem fs = getFileSystem();
            System.out.println("oldFilePath="+oldPath);
            System.out.println("oldFilePath="+newPath);
            // 创建目录
            fs.rename(new Path(oldPath),new Path(newPath));
            //释放资源
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public   boolean existDir(String filePath, boolean create){
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

    public List<String> listAllString (String datasetUserandNameUrl) throws IOException {
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

    //包含了他的路径
    public void copyFileToHDFS(String sourceFileName, String tmpPath)throws Exception{
        FileSystem fs = getFileSystem();
        Path source = new Path(sourceFileName);
        String filePath = hdfsurl+"/"+tmpPath+"/"+source.getName();
        Path  finalPath = new Path(filePath);
        if(!fs.exists(finalPath) && !fs.isDirectory(finalPath)){
            fs.copyFromLocalFile(false,true,source,finalPath);
        }
        fs.close();
    }

    public void deletedir(String datasetUserandNameUrl) {
        try {
            // 返回FileSystem对象
            FileSystem fs = getFileSystem();

            String hdfsUri = hdfsurl;
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

    public String GetFileSize(String Path){
        return GetFileSize(new File(Path));
    }
    public String GetFileSize(File file){
        String size = "";
        if(file.exists() && file.isFile()){
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) +"GB";
            }
        }else if(file.exists() && file.isDirectory()){
            size = "";
        }else{
            size = "0BT";
        }
        return size;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }


}
