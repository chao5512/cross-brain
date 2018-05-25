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
    //数据集英文名称
    @Column(name = "dataset_en_name")
    private static String dataSetEnglishName;
    //数据集上传文件状态
    @Column(nullable = false,name = "dataset_status")
    private static String dataSetStatus;
    //数据集公开状态
    @Column(nullable = false,name = "dataset_if_public")
    private static String dataSetPower;
    //数据集最后修改时间
    @Column(nullable = false,name = "dataset_updated_time")
    private static String dataSetLastUpdateTime;
    //数据集最后修改描述
    @Column(nullable = true,name = "dataset_modidied_info")
    private static String dataSetUpdateDesc;
    //数据集排序方式
    @Column(nullable = false,name = "dataset_rank_mode")
    private static String dataSetSortBY;

    @Column(name = "dataset_sort_type")
    private static String dataSetSortType;
    //数据集容量大小
    @Column(nullable = false,name = "dataset_size")
    private static int maxContener;
    //数据及当前文件数量
    @Column(nullable = false,name = "dataset_filecount")
    private static int filesCount;
    @Column(name = "dataset_basic_desc")
    //当且仅当  system表获取信息时使用
    private static String dataSetBasicDesc;
    //创建时间
    @Column(name = "dataset_create_time")
    private static String dataSetCreateTime;
    //中文名称
    @Column(name = "dataset_cn_name")
    private static String dataSetName ;
    //保存路径
    @Column(name = "dataset_storeurl")
    private static String dataSetStoreUrl;
    //hive 表名
    @Column(name = "dataset_hivetableName")
    private static String dataSetHiveTableName;
    //H表ID
    @Column(name = "dataset_hivetableId")
    private static String dataSetHiveTableId;
    //数据集数据类型
    @Column(name = "dataset_dataType")
    private static String datatype;
    // H表创建时的属性
    private static Hiveinfo hiveinfo;
    //数据集唯一标识的时间错
    private long createTimetamp;

    private long contentTimeTmp;


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
    public String getDatasetId(){
        return dataSetId;
    }
    public void setDatasetId() {
        dataSetId = dataSetEnglishName+"_"+createTimetamp;
    }


    public long getCreateTimetamp() {
        return createTimetamp;
    }
    public void setCreateTimetamp(long createTimetamp) {
        this.createTimetamp = createTimetamp;
    }


    //数据集名称(en)
    public String getDataSetEnglishName() {
        return dataSetEnglishName;
    }
    public void setDataSetEnglishName(String dataSetEnglishName) {
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

    //创建时间
    public Long getContentTimeStamp(){
        return contentTimeTmp;
    }
    public void setContentTimeStamp(){
        contentTimeTmp = System.currentTimeMillis();
    }
    public String getDataSetCreateTime( ) {
        long timetmp = createTimetamp;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
        dataSetCreateTime = sd;
        return dataSetCreateTime;
    }
    //修改时间
    public String getDatasetUpdatetime(long timetmp) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
        dataSetLastUpdateTime = sd;
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
    public String getDataSetSortBY() {
        return dataSetSortBY;
    }
    public void setDataSetSortBY(String datasetSortBy) { dataSetSortBY= datasetSortBy; }

    //排序升降序方式
    public  String getDataSetSortType() {
        return dataSetSortType;
    }
    public  void setDataSetSortType(String dataSetSortType) {
        DataSet.dataSetSortType = dataSetSortType;
    }


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


    //数据集中文
    public String getDataSetName() {
        return dataSetName;
    }
    public void setDataSetName(String dataSetName) {
        DataSet.dataSetName = dataSetName;
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
