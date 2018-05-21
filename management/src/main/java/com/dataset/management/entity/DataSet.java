package com.dataset.management.entity;

;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name = "dataset_basic_info")
@NamedNativeQueries({
        @NamedNativeQuery(name = "DataSet.updateUpdateTime",
        query = "update DataSet ds Set ds.dataSetLastUpdateTime= :dataSetLastUpdateTime where ds.dataSetId = :datasetId")})

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



    //自动创建的ID
    public static int getId() {
        return id;
    }
    public static void setId(int id) {
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

}
