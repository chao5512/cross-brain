package com.dataset.management.service;

import com.dataset.management.consts.DataSetSystemConsts;
import com.dataset.management.entity.DataSet;
import com.sun.tools.doclets.formats.html.PackageTreeWriter;
import org.apache.calcite.avatica.Meta;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
@Service
public class HdfsService {

    private String hdsfbasic = DataSetSystemConsts.HDFS_BASIC_URL;

    private String user = DataSetSystemConsts.DATASET_OWNER;

    private Logger logger = LoggerFactory.getLogger(HdfsService.class);

    private DataSet dataSet;

    @Autowired
    HiveService hiveService;

    private String newStoreUrl;

    //默认的 路径
    private String conterHdfsUrl(){
        newStoreUrl = hdsfbasic+user+hiveService.getHiveTableName();
        return hdsfbasic+user+hiveService.getHiveTableName();
    }

    public void uploadFiles(List<String> newFiles)throws IOException{
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

    public List<String> datasetHdsfFiles() throws IOException{
        String url = conterHdfsUrl();
        Configuration configuration = new Configuration();
        Path dfspath = new Path(url);
        FileSystem fs = FileSystem.get(dfspath.toUri(),configuration);
        FileStatus[] stats = fs.listStatus(dfspath);
        List<String> names = new ArrayList<>();
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

    public void deleteFiles(List<String> files)throws IOException{
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

    public void deleteDatasetDir()throws IOException{
        String url = conterHdfsUrl();
        Configuration configuration = new Configuration();
        Path dfspath = new Path(url);
        FileSystem fs = FileSystem.get(dfspath.toUri(),configuration);
        fs.delete(dfspath,true);
        fs.close();
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public DataSet getDataSet() {
        return dataSet;
    }
    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    /***
     * 更改存储路径；
     * */
    public void setStorerUrl( String newstoreurl){
        newStoreUrl = newstoreurl;
    }
    public String getNewStoreUrl(){
        return newStoreUrl;
    }

}
