
package com.bonc.pezy.service.impl;
import com.bonc.pezy.config.HdfsConfig;
import com.bonc.pezy.service.HdfsModel;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;


@Service
public class HdfsModelImpl implements HdfsModel {

    @Autowired
    private HdfsConfig hdfsConfig;

    private  String hdfs;

    @Override
    public InputStream downLoadFile(String hdfsPath) throws IOException {
        FileSystem fileSystem = getFileSystem();
        InputStream inputStream = fileSystem.open(new Path(hdfsPath));
        return inputStream;
    }

    @Override
    public FSDataInputStream readHdfsFiles(String hdfsUrl)throws IOException{
        FileSystem fileSystem = getFileSystem();
        Path hdfsPath = new Path(hdfsUrl);
        FileStatus fileStatuses = fileSystem.getFileStatus(hdfsPath);
        System.out.println("处理当前文件："+fileStatuses.getPath().getName());
        FSDataInputStream fsDataInputStream = fileSystem.open(fileStatuses.getPath());
        return fsDataInputStream;
    }

    private FileSystem getFileSystem() {
        Configuration conf = new Configuration();
        FileSystem fs = null;
        hdfs = hdfsConfig.getHdfsUrl()+":"+hdfsConfig.getHdfsProt();
        if(StringUtils.isBlank(hdfs)){
            try {
                fs = FileSystem.get(conf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{

            try {
                URI uri = new URI(hdfs.trim());
                fs = FileSystem.get(uri,conf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fs;
    }

}
