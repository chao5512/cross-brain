package com.bonc.pezy.service;

import org.apache.hadoop.fs.FSDataInputStream;
import org.springframework.web.multipart.MultipartFile;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipOutputStream;

public interface HdfsModel {
    public InputStream downLoadFile(String hdfsUrl)throws IOException;

    public FSDataInputStream readHdfsFiles(String hdfsUrl)throws IOException;

    //上传文件到hdfs
    public void fileUpload(MultipartFile file,String destPath) throws IOException;

    public FileStatus[] allFiles(String uri)throws IOException;

    public FileSystem fs(String dir)throws IOException;

    public FileSystem getFileSystem()throws IOException;

    public boolean hdfszip(String hdfsModelDirUri,ZipOutputStream outZip)throws IOException;
}
