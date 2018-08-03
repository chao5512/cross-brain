package com.dataset.management.entity;

;

import org.springframework.stereotype.Controller;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity(name = "dataset_basic_info")
public class DataSet implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "user_name")
    private String userName;
    @Column(name ="user_Id")
    private int userId;
    //数据集英文名称
    @Column(name = "dataset_english_name")
    private String dataSetEnglishName;
    //数据集上传文件状态
    @Column(name = "dataset_status")
    private String dataSetStatus;
    //数据集公开状态
    @Column(name = "dataset_if_public")
    private String dataSetPower;
    //数据集最后修改时间
    @Column(name = "dataset_updated_time")
    private String dataSetLastUpdateTime;
    //数据集最后修改描述
    @Column(name = "dataset_modified_info")
    private String dataSetUpdateDesc;
    //数据集排序方式
    @Column(name = "dataset_rank_mode")
    private String dataSetSortBY;
    @Column(name = "dataset_sort_type")
    private String dataSetSortType;
    //数据集容量大小
    @Column(name = "dataset_size")
    private int dataSetSize;
    //数据及当前文件数量
    @Column(name = "dataset_filecount")
    private  int dataSetFilesCount;
    @Column(name = "dataset_basic_desc")
    //当且仅当  system表获取信息时使用
    private String dataSetBasicDesc;
    //创建时间
    @Column(name = "dataset_create_time")
    private String dataSetCreateTime;
    //中文名称
    @Column(name = "dataset_name")
    private String dataSetName ;
    //保存路径
    @Column(name = "dataset_storeurl")
    private String dataSetStoreUrl;
    //hive 表名
    @Column(name = "dataset_hivetable_name")
    private String dataSetHiveTableName;
    //H表ID
    @Column(name = "dataset_hivetable_id")
    private  String dataSetHiveTableId;
    //数据集数据类型
    @Column(name = "dataset_data_type")
    private String datatype;

    //模型路径
    @Column(name ="models_url")
    private String modelsUrl;

    //自动创建的ID
    public int getId() {
        return id;
    }
    public void setId(int ids) {
        id = ids;
    }

    //用户名称
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    //数据集名称(en)
    public String getDataSetEnglishName() {
        return dataSetEnglishName;
    }
    public void setDataSetEnglishName(String datasetEnglishName) {
        dataSetEnglishName = datasetEnglishName;
    }

    //数据集当前上传文件状态：
    public String getDataSetStatus() {
        return dataSetStatus;
    }
    public void setDataSetStatus(String datasetStatus) {
        dataSetStatus = datasetStatus;
    }

    //数据集公开状态
    public String getDataSetPower() {
        return dataSetPower;
    }
    public void setDataSetPower(String datasetPower) {
        dataSetPower = datasetPower;
    }

    public String getDataSetCreateTime( ) {
        return dataSetCreateTime;
    }
    public void setDataSetCreateTime(String newtiem){
        dataSetCreateTime = newtiem;
    }

    //修改时间
    public String getDataSetLastUpdateTime() {
        return dataSetLastUpdateTime;
    }
    public void setDataSetLastUpdateTime( String datasetLastUpdateTime ) {
        dataSetLastUpdateTime = datasetLastUpdateTime;
    }

    public String getDataSetUpdateDesc() {
        return dataSetUpdateDesc;
    }
    public void setDataSetUpdateDesc(String datasetUpdateDesc) {
        dataSetUpdateDesc = datasetUpdateDesc;
    }

    //排序方式
    public String getDataSetSortBY() {
        return dataSetSortBY;
    }
    public void setDataSetSortBY(String datasetSortBy) { dataSetSortBY= datasetSortBy; }

    //排序升降序方式
    public  String getDataSetSortType() {
        return dataSetSortType;
    }
    public  void setDataSetSortType(String datasetSortType) {
        dataSetSortType = datasetSortType;
    }


    //数据及大小
    public int getDataSetSize() {
        return dataSetSize;
    }
    public void setDataSetSize(int maxContener1) {
        dataSetSize= maxContener1;
    }

    //文件数
    public int getDataSetFileCount() {
        return dataSetFilesCount;
    }
    public void setDataSetFileCount(int fileCount) {
        dataSetFilesCount = fileCount;
    }


    public String getDataSetBasicDesc() {
        return dataSetBasicDesc;
    }
    public void setDataSetBasicDesc(String datasetBasicDesc) {
        dataSetBasicDesc = datasetBasicDesc;
    }


    //数据集中文
    public String getDataSetName() {
        return dataSetName;
    }
    public void setDataSetName(String datasetName) {
        dataSetName = datasetName;
    }

    public String getDataSetStoreUrl() {
        return dataSetStoreUrl;
    }
    public void setDataSetStoreUrl(String datasetStoreUrl) {
        dataSetStoreUrl = datasetStoreUrl;
    }

    public String getDataSetHiveTableName() {
        return dataSetHiveTableName;
    }
    public void setDataSetHiveTableName(String datasetHiveTableName) {
       dataSetHiveTableName = datasetHiveTableName;
    }

    public String getDataSetHiveTableId() {
        return dataSetHiveTableId;
    }
    public void setDataSetHiveTableId(String datasetHiveTableId) {
        dataSetHiveTableId = datasetHiveTableId;
    }

    public String getDatatype() {
        return datatype;
    }
    public void setDatatype(String datatype1) {
        datatype = datatype1;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getModelsUrl() {
        return modelsUrl;
    }
    public void setModelsUrl(String modelsUrl) {
        this.modelsUrl = modelsUrl;
    }

}
