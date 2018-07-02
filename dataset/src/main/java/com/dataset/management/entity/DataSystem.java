package com.dataset.management.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(name = "dataset_system_info")
public class DataSystem implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "dataset_id")
    private  int dataSetId ;
    @OneToOne
    @JoinColumn(name = "dataset_id",insertable = false,updatable = false)
    private DataSet dataSet;
    @Column(name = "dataset_name")
    private String dataSetName ;
    @Column(name = "dataset_english_name")
    private String dataSetEngListName;
    @Column(name = "created_time")
    private String dataSetCreateTime;
    @Column(name = "path")
    private String dataSetStoreUrl;
    @Column(name = "description")
    private String dataSetDesc;
    @Column(name = "hive_table_name")
    private String dataSetHiveTableName;
    @Column(name = "hive_table_Id")
    private String dataSetHiveTableId;
    @Column(name = "dataset_sort_by")
    private String dataSetSystemSortBy;
    @Column(name ="dataset_sort_type")
    private String dataSetSortType;
    @Column(name ="user_name")
    private String userName;
    @Column(name ="user_Id")
    private int userId;

//    //用户名
//    public String getDataSetUserId() {
//        return dataSetUserId;
//    }
//    public void setDataSetUserId(String dataSetid){
//        dataSetUserId = dataSetid;
//    }


    public int getDataSetId() {
        return dataSetId;
    }
    public void setDataSetId(int dataSetId) {
        this.dataSetId = dataSetId;
    }

    public DataSet getDataSet() {
        return dataSet;
    }
    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
