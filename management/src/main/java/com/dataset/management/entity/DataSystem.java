package com.dataset.management.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(name = "dataset_system_info")
public class DataSystem implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,name = "datasetId")
    private static String dataSetId ;
//    @Column(nullable = false,name = "userId")
//    private static String dataSetUserId ;
    @Column(nullable = true,name = "ch_name")
    private static String dataSetName ;
    @Column(nullable = false,name = "en_name")
    private static String dataSetEngListName;
    @Column(nullable = false,name = "create_time")
    private static String dataSetCreateTime;
    @Column(nullable = false,name = "path")
    private static String dataSetStoreUrl;
    @Column(nullable = true,name = "description")
    private static String dataSetDesc;
    @Column(nullable = false,name = "hive_table_name")
    private static String dataSetHiveTableName;
     @Column(nullable = false,name = "hive_table_Id")
    private String dataSetHiveTableId;
    @Column(name = "datasetSortBy")
    private String dataSetSystemSortBy;
    @Column(name ="datasetSortType")
    private String dataSetSortType;

    private static long DATASET_CREATE_TIMETMP ;

//    //用户名
//    public String getDataSetUserId() {
//        return dataSetUserId;
//    }
//    public void setDataSetUserId(String dataSetid){
//        dataSetUserId = dataSetid;
//    }

    public String getDataSetId() {
        return dataSetId;
    }
    public void setDataSetId(String datasetId) {
        DataSystem.dataSetId = datasetId;
    }


    //数据集名称
    public String getDatasetName(){
        return dataSetName;
    }
    public void setDatasetName(String datasetName){
        dataSetName = datasetName;
    }

    //数据集英文名
    public String getDatasetEnglishName() {
        return dataSetEngListName;
    }
    public void setDatasetEnglishName(String datasetEnglishName) {
        dataSetEngListName = datasetEnglishName;
    }


    //数据集存档位子
    public String getDatasetStoreurl() {
        return dataSetStoreUrl;
    }
    public void setDatasetStoreurl(String datasetStoreurl) {
        dataSetStoreUrl = datasetStoreurl;
    }

    //数据集描述
    public String getDatasetDesc(){
        return dataSetDesc;
    }
    public void setDatasetDesc(String datasetDesc) {
        dataSetDesc = datasetDesc;
    }

    //获取转换后的时间
    public String getDatasetCreateDate() {
        return dataSetCreateTime;
    }
    public void setDatasetCreateDate(String sd){
        dataSetCreateTime = sd;
    }


    //对应hive 表
    public String getDatasetHiveTablename() {
        return dataSetHiveTableName;
    }
    public void setDatasetHiveTablename(String hiveTablename) {
        dataSetHiveTableName = hiveTablename;
    }

    //hive表ID
    public String getDataSetHiveTableId() {
        return dataSetHiveTableId;
    }
    public void setDataSetHiveTableId(String datasetHiveTableId) {
        dataSetHiveTableId = datasetHiveTableId;
    }


    public String getDataSetSystemSortBy() {
        return dataSetSystemSortBy;
    }
    public void setDataSetSystemSortBy(String dataSetSystemSortBy) {
        this.dataSetSystemSortBy = dataSetSystemSortBy;
    }


    public String getDataSetSortType() {
        return dataSetSortType;
    }
    public void setDataSetSortType(String dataSetSortType) {
        this.dataSetSortType = dataSetSortType;
    }

}
