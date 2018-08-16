
package com.bonc.pezy.service.impl;
import com.bonc.pezy.config.HdfsConfig;
import com.bonc.pezy.service.HdfsModel;
import com.bonc.pezy.util.Upload;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;


@Service
public class HdfsModelImpl implements HdfsModel {

    @Autowired
    private HdfsConfig hdfsConfig;

    private  String hdfs;

    private static final Logger logger = LoggerFactory.getLogger(Upload.class);

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

    /**
     * 功能描述:上传文件
     * @param file
     * @param destPath
     * @return: void
     * @auther: 王培文
     * @date: 2018/8/16 15:16
     */
    @Override
    public void fileUpload(MultipartFile file,String destPath) throws IOException {
        BufferedOutputStream out = null;
        try {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                logger.info("原始文件名:" + originalFilename);
                out = new BufferedOutputStream(
                        new FileOutputStream(
                                new File(originalFilename)
                        )
                );
                out.write(file.getBytes());
                out.flush();
                logger.info("目的地址：" + destPath);
                //uploadPath为最终图片保存的路径
                String uploadPath = Upload.uploadFile(originalFilename, destPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
