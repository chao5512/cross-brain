package com.dataset.management.service;

import com.dataset.management.consts.DataSetSystemConsts;
import com.sun.tools.doclets.formats.html.PackageTreeWriter;
import org.apache.calcite.avatica.Meta;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class HdfsService {

    private String hdsfbasic = DataSetSystemConsts.HDFS_BASIC_URL;

    private String user = DataSetSystemConsts.DATASET_OWNER;

    private Logger logger = LoggerFactory.getLogger(HdfsService.class);

    public String conterHdfsUrl(){
        return hdsfbasic+user;
    }

    public void uploadFiles(List<String> newFiles, String hdfsPath)throws IOException{
        String url = conterHdfsUrl();
        Configuration configuration = new Configuration();
        Path dfspath = new Path(url);
        FileSystem fs = FileSystem.get(dfspath.toUri(),configuration);
        for(String file: newFiles){
            Path filepath = new Path(file);
            logger.info("上传文件："+file);
            fs.copyFromLocalFile(filepath,dfspath);
        }
        fs.close();
    }

    public void deleteFiles(List<String> files,String hdfsPath)throws IOException{
        String url = conterHdfsUrl();
        Configuration configuration = new Configuration();
        Path dfspath = new Path(url);
        FileSystem fs = FileSystem.get(dfspath.toUri(),configuration);
        for(String file:files ){
            Path filepath = new Path(file);
            logger.info("删除文件："+file);
            fs.delete(filepath,true);
        }
        fs.close();
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }



}
