
package com.bonc.pezy.service.impl;
import com.bonc.pezy.config.HdfsConfig;
import com.bonc.pezy.service.HdfsModel;
import com.bonc.pezy.util.Upload;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.hadoop.record.meta.TypeID.RIOType.BUFFER;


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
        FSDataInputStream fsDataInputStream = fileSystem.open(new Path(hdfsUrl));
        return fsDataInputStream;
    }
    @Override
    public FileStatus[] allFiles(String uri)throws IOException{
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), conf);
        FileStatus[] fileStatuses = fs.listStatus(new Path(uri));
        return fileStatuses;
    }
    public FileSystem fs(String dir)throws IOException{
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dir), conf);
        return fs;
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

    @Override
    public FileSystem getFileSystem() {
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

    public boolean hdfszip(String uri,ZipOutputStream outZip)throws IOException{
        FileSystem fs = getFileSystem();
        FileStatus[] fileStatuses = fs.listStatus(new Path(uri));
        if(fileStatuses.length ==0){
            logger.info(uri+"   是空目录");
            outZip.putNextEntry(new ZipEntry(uri+File.separator)); //添加文件后最符号 区分是目录
        }
        Path sourceFilePath;
        try {
            for (int i = 0; i < fileStatuses.length; i++) {
                sourceFilePath = new Path(uri + fileStatuses[i].getPath().getName());
                System.out.println(sourceFilePath.getName());
                if(fileStatuses[i].isDirectory()){
                    System.out.println(fileStatuses[i].getPath().toString()+"  是路径  ");
                    hdfszip(fileStatuses[i].getPath().toString(),outZip);
                }else {
                    FSDataInputStream in = fs.open(fileStatuses[i].getPath());
                    logger.info("文件路径："+fileStatuses[i].getPath().toString());
                    int bytesRead = 0;
                    //建立檔案的 entry
                    ZipEntry entry = new ZipEntry(fileStatuses[i].getPath().toString());
                    outZip.putNextEntry(entry);
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = in.read(buffer)) != -1) {
                    outZip.write(buffer, 0, bytesRead);
                    }
                    in.close();
                }
            }
            outZip.flush();
            outZip.close();
            return  true;
        } catch(Exception e) {
            outZip.close();
            e.printStackTrace();
            return false;
        }
    }
}
