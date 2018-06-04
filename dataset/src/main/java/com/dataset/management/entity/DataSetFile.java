package com.dataset.management.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity(name = "dataset_file_info")
public class DataSetFile implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
//    @Column(name = "fileId")
//    private String fileId;
    @Column(name = "name")
    private String fileName;
    @Column(name = "dataset_id")
    private int dataSetId;

//    @JoinColumn(name = "dataset_id")
//    private  DataSet bpoDataSet;
    @Column(name = "path")
    private  String filePath;
    @Column(name = "upload_time")
    private String uploadTime;
    @Column(name = "file_sort_by")
    private String fileSortBy;
    @Column(name = "file_sort_type")
    private String fileSortType;
    @Column(name = "description")
    private String fileDesc;
    @Column(name = "size")
    private String filesize;


    //文件名
    public  String getFileName(){
        return fileName;
    }
    public  void setFileName(String fileName1){
        fileName = fileName1;
    }

    //文件ID
    public int getId() {
        return id;
    }
    public void setId(int Id) {
        id = Id;
    }

    //date时间格式
    public  void setOnloadTimeDate( String  onloadTimetmp){
        uploadTime = onloadTimetmp;
    }
    public String getOnloadTimedate() {
        return uploadTime;
    }

    //文件路径
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath1) {
        filePath = filePath1;
    }

    //文件描述
    public String getFileDesc(){
        return fileDesc;
    }
    public void setFileDesc(String fileDesc1) {
        fileDesc = fileDesc1;
    }

    //文件大小
    public  String getFileSize() {
        return filesize;
    }
    public  void setFileSize(String fileSize) {
        filesize = fileSize;
    }

    public String getFileSortBy() {
        return fileSortBy;
    }
    public void setFileSortBy(String fileSortBy) {
        this.fileSortBy = fileSortBy;
    }

    public String getFileSortType() {
        return fileSortType;
    }
    public void setFileSortType(String fileSortType) {
        this.fileSortType = fileSortType;
    }

    public int getDataSetId() {
        return dataSetId;
    }
    public  void setDataSetId(int datasetId) {
        dataSetId = datasetId;
    }

}
