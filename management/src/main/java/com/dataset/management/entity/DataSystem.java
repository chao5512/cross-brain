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
    @Column(nullable = false,name = "userId")
    private static String dataSetUserId ;
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
    @Column(nullable = false,name = "dataType")
    private static String datatype;

    private static long DATASET_CREATE_TIMETMP ;

    //用户名
    public String getDataSetUserName() {
        return dataSetUserId;
    }
    public void setDataSetUserName(String dataSetid){
        dataSetUserId = dataSetid;
    }

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

    //时间戳  用于创建ID 字段
    public long getDatasetCreateTimetmp() {
        long time = System.currentTimeMillis();     //当前时间戳
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));   // 时间戳转换成时间
        setDatasetCreateDate(sd);                   //同时变更时间结构
        DATASET_CREATE_TIMETMP = time;
        return DATASET_CREATE_TIMETMP ;
    }

    //获取转换后的时间
    public String getDatasetCreateDate() {
        return dataSetCreateTime;
    }
    private void setDatasetCreateDate(String sd){
        dataSetCreateTime = sd;
    }

    //数据类型
    public String getDataType() {
        return datatype;
    }
    public void setDataType(String DATATYPE) {
        datatype = DATATYPE;
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
        this.dataSetHiveTableId = datasetHiveTableId;
    }


}
