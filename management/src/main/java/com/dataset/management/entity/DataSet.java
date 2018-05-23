package com.dataset.management.entity;

;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Controller;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity(name = "dataset_basic_info")
public class DataSet implements Serializable{

    private static String DATASET_OWNER;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false,name = "id")
    private static int id;
    @Column(nullable = false,name = "datasetId")
    private static String dataSetId ;
    @ManyToOne
    @JoinColumn(name = "datasetId")
    private static DataSystem dataSetSystem;
    @Column(name = "dataset_en_name")
    private static String dataSetEnglishName;
    @Column(nullable = false,name = "status")
    private static String dataSetStatus;
    @Column(nullable = false,name = "if_public")
    private static String dataSetPower;
    @Column(nullable = false,name = "updated_time")
    private static String dataSetLastUpdateTime;
    @Column(nullable = true,name = "modidied_info")
    private static String dataSetUpdateDesc;
    @Column(nullable = false,name = "rank_mode")
    private static String dataSetSortBY;
    @Column(nullable = false,name = "size")
    private static int maxContener;
    @Column(nullable = false,name = "number")
    private static int filesCount;

    private static List<DataSetFile> listfiles;

    //当且仅当  system表获取信息时使用
    private static String dataSetBasicDesc;

    private static String dataSetCreateTime;

    private static String dataSetName ;

    private static String dataSetEngListName;

    private static String dataSetStoreUrl;

    private static String dataSetHiveTableName;

    private static String dataSetHiveTableId;

    private static String datatype;

    private static Hiveinfo hiveinfo;




    //自动创建的ID
    public int getId() {
        return id;
    }
    public void setId(int id) {
        DataSet.id = id;
    }

    //数据集创建者
    public String getDatasetOwner() {
        return DATASET_OWNER;
    }
    public void setDatasetOwner(String datasetOwner) {
        DATASET_OWNER = datasetOwner;
    }

    // 数据集ID
    public String getDatasetId() {
        return dataSetId;
    }
    public void setDatasetId(String datasetId) {
       dataSetId = datasetId;
    }

    //数据集名称
    public static String getDataSetEnglishName() {
        return dataSetEnglishName;
    }
    public static void setDataSetEnglishName(String dataSetEnglishName) {
        DataSet.dataSetEnglishName = dataSetEnglishName;
    }

    //数据集当前上传文件状态：
    public String getDatasetStatus() {
        return dataSetStatus;
    }
    public void setDatasetStatus(String datasetStatus) {
        dataSetStatus = datasetStatus;
    }

    //数据集公开状态
    public String getDatasetPower() {
        return dataSetPower;
    }
    public void setDatasetPower(String datasetPower) {
        dataSetPower = datasetPower;
    }

    //数据集文件列表
    public List<DataSetFile> getListfiles() {
        return listfiles;
    }
    public void setListfiles(List<DataSetFile> listfiles) {
        DataSet.listfiles = listfiles;
    }


    //修改时间
    public String getDatasetUpdatetime() {
        return dataSetLastUpdateTime;
    }
    public void setDatasetUpdatetime(String datasetUpdatetime) {
        dataSetLastUpdateTime = datasetUpdatetime;
    }

    public String getDataSetUpdateDesc() {
        return dataSetUpdateDesc;
    }
    public void setDataSetUpdateDesc(String datasetUpdateDesc) {
        DataSet.dataSetUpdateDesc = datasetUpdateDesc;
    }

    public DataSystem getDataSetSystem() {
        return dataSetSystem;
    }
    public void setDataSetSystem(DataSystem dataSetSystem) {
        DataSet.dataSetSystem = dataSetSystem;
    }

    //排序方式
    public String getSortType() {
        return dataSetSortBY;
    }
    public void setSortType(String datasetSortBy) { dataSetSortBY= datasetSortBy; }

    //数据及大小
    public int getMaxContener() {
        return maxContener;
    }
    public void setMaxContener(int maxContener1) {
        maxContener = maxContener1;
    }

    //文件数
    public int getFileCount() {
        return filesCount;
    }
    public void setFileCount(int fileCount) {
        filesCount = fileCount;
    }


    public String getDataSetBasicDesc() {
        return dataSetBasicDesc;
    }
    public void setDataSetBasicDesc(String dataSetBasicDesc) {
        DataSet.dataSetBasicDesc = dataSetBasicDesc;
    }

    public Long getTimeStamp(){
        return System.currentTimeMillis();
    }
    public String getDataSetCreateTime() {
        long timetmp = getTimeStamp();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
        dataSetCreateTime = sd;
        return dataSetCreateTime;
    }
    public void setDataSetCreateTime(String datasetCreateTime) {
        dataSetCreateTime = datasetCreateTime;
    }


    public String getDataSetName() {
        return dataSetName;
    }
    public void setDataSetName(String dataSetName) {
        DataSet.dataSetName = dataSetName;
    }

    public String getDataSetEngListName() {
        return dataSetEngListName;
    }
    public void setDataSetEngListName(String dataSetEngListName) {
        DataSet.dataSetEngListName = dataSetEngListName;
    }

    public String getDataSetStoreUrl() {
        return dataSetStoreUrl;
    }
    public void setDataSetStoreUrl(String dataSetStoreUrl) {
        DataSet.dataSetStoreUrl = dataSetStoreUrl;
    }

    public String getDataSetHiveTableName() {
        return dataSetHiveTableName;
    }
    public void setDataSetHiveTableName(String dataSetHiveTableName) {
        DataSet.dataSetHiveTableName = dataSetHiveTableName;
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
    public void setDatatype(String datatype) {
        DataSet.datatype = datatype;
    }

    public Hiveinfo getHiveinfo() {
        return hiveinfo;
    }
    public void setHiveinfo(Hiveinfo hiveinfo) {
        DataSet.hiveinfo = hiveinfo;
    }

}
