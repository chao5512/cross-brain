package com.dataset.management.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity(name = "dataset_files_info")
public class DataSetFile implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,name = "fileId")
    private static String fileId;
    @Column(nullable = false,name = "name")
    private static String fileName;
    @Column(nullable = false,name = "datasetId")
    private static String dataSetId;
    @ManyToOne
    @JoinColumn(name = "datasetId")
    private static DataSet bpoDataSet;
    @Column(name = "path")
    private static String filePath;
    @Column(name = "upload_time")
    private static String uploadTime;

    @Column(name = "fileSortBy")
    private String fileSortBy;
    @Column(name = "fileSortType")
    private String fileSortType;

    @Column(nullable = true,name = "description")
    private static String fileDesc;

    @Column(nullable = true,name = "size")
    private static String filesize;

    private static long UPLOAD_TIMETMP;
//    private static String FILE_USE_STATUS;
//
//    private static String FILE_OWNER;
//
//    private static String LAST_MODEL_NAME;
//
//    private static List<DataSetColumn> columns;
//
//    private static String fileType;
//    @Column(nullable = true,name = "update_description")
//    private static String fileUpdateDesc;
//    private static String filePower;
//    @Column(name ="last_update_time")
//    private static String lastUpadteTime;

    //文件名
    public  String getFileName(){
        return fileName;
    }
    public  void setFileName(String fileName1){
        fileName = fileName1;
    }


    //时间戳
    public Long getOnloadTimetmp() {
        UPLOAD_TIMETMP = System.currentTimeMillis();
        return UPLOAD_TIMETMP;
    }

    //文件ID
    public String getFileId() {
        return fileId;
    }
    public void setFileId(String fileID) {
        fileId = fileID;
    }

    //date时间格式
    public  void setOnloadTimeDate(long onloadTimetmp){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        uploadTime = sdf.format(new Date(Long.parseLong(String.valueOf(onloadTimetmp))));
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

//    //文件最后修改时间
//    public String getLastUpdateTime() {
//        return lastUpadteTime;
//    }
//    public void setLastUpdateTime(String lastUpdateTime) {
//        lastUpadteTime = lastUpdateTime;
//    }

    //文件描述
    public String getFileDesc(){
        return fileDesc;
    }
    public void setFileDesc(String fileDesc1) {
        fileDesc = fileDesc1;
    }

//    //文件修改描述
//    public String getFileUpdateDesc(){
//        return fileUpdateDesc;
//    }
//    public void setFileUpdateDesc(String fileUpdateDesc1) {
//        fileUpdateDesc = fileUpdateDesc1;
//    }

    //文件权限
//    public String getFilePower() {
//        return filePower;
//    }
//    public void setFilePower(String filePower1) {
//        filePower = filePower1;
//    }

    public static DataSet getBpoDataSet() {
        return bpoDataSet;
    }
    public static void setBpoDataSet(DataSet bpoDataSet) {
        DataSetFile.bpoDataSet = bpoDataSet;
    }

//
//    //文件使用状态
//    public String getFileUseStatus() {
//        return FILE_USE_STATUS;
//    }
//    public void setFileUseStatus(String fileUseStatus) {
//        FILE_USE_STATUS = fileUseStatus;
//    }
//
//    //文件所属者
//    public String getFileOwner() {
//        return FILE_OWNER;
//    }
//    public void setFileOwner(String fileOwner) {
//        FILE_OWNER = fileOwner;
//    }

    //文件所属数据集
    public String getBpodataSets() {
        return dataSetId;
    }
    public void setBpodataSets(String bpodataSet1) {
        dataSetId = bpodataSet1;
    }

//    //文件包含的列列表
//    public List<DataSetColumn> getColumns() {
//        return columns;
//    }
//    public void setColumns(List<DataSetColumn> columns) {
//        DataSetFile.columns = columns;
//    }
//
//    //文件类型
//    public String getFileType() {
//        return fileType;
//    }
//    public void setFileType(String fileType1) {
//        fileType = fileType1;
//    }
//
//    //文件最新的使用模型
//    public String getLastModelName() {
//        return LAST_MODEL_NAME;
//    }
//    public void setLastModelName(String lastModelName) {
//        LAST_MODEL_NAME = lastModelName;
//    }

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
}
