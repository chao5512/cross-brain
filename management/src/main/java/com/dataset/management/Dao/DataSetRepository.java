package com.dataset.management.Dao;

import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DataSetRepository extends JpaRepository<DataSet,String> {
    /**
     * 保存数据集整个信息
     * */
    public DataSet save(DataSet dataSet);

    /**
     * 定位dataset 方式
     * */
    public DataSet findByDataSetId(String datasetId);

    public DataSet findByDataSetName (String datasetName);

    /**
     * 数据集概览
     * */
    public List<DataSet> findAll(Sort sort);

    public DataSet findFirst();

    /**
     * 更改数据集多个属性
     * */
    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSet dst SET " +
            "dst.dataSetEnglishName = ?1," +
            "dst.dataSetName =?2," +
            "dst.dataSetStoreUrl = ?3," +
            "dst.dataSetBasicDesc = ?4," +
            "dst.dataSetHiveTableName = ?5," +
            "dst.dataSetSortBY =?6," +
            "dst.dataSetSortType =?7," +
            "dst.datasetPower =?8," +
            "dst.dataSetUpdateDesc =?9," +
            "dst.maxContener =?10," +
            "dst.datatype =?11 where dst.datasetId = ?12")
    public void updateAll(String en_datasetName,
                          String ch_datasetName,
                          String path,
                          String basicDesc,
                          String hivetableName,
                          String sortBy,
                          String sortType,
                          String powerStatus,
                          String updateDesc,
                          int newMax,
                          String dataType,
                          String datasetId);
    /**
     * 更改数据集名称
     * */
    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSet dst SET " +
            "dst.dataSetEngListName = ?1," +
            "dst.dataSetName = ?2, " +
            "dst.sortType = ?3 where dst.datasetId = ?4" )
    public void updateDataSetName(String englishName,String chinaName,String sortBy,String dataSetId);

    /**
     * 更改数据集公开状态
     * */
    public void updateDataSetPowerStatus(String powerStatus,String datasetId);

    /**
     * 更改数据集排序方式
     * */
    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSet dst SET " +
            "dst.dataSetSortBY= ?1 where dst.dataSetSortBY = dst.dataSetSortBY")
    public void updateDataSetSortBy(String newSortBy);

    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSet dst SET " +
            "dst.dataSetSortType= ?1 where dst.dataSetSortType = dst.dataSetSortType")
    public void updateDataSetSortType(String sortType);

    /**
     * 更改数据集修改时间
     * */
    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSet dst SET " +
            "dst.datasetUpdatetime= ?1 where dst.datasetId = ?2")
    public void updateDataSetLastUpdateTime(String newTime,String datasetId);

    /**
     * 更改数据集描述
     * */
    public void updateDataSetDesc(String newDesc,String datasetId);

    /**
     * 更改数据集上限
     * */
    public void updateDataSetMaxContener(int newMax,String datasetId);

    /**
     * 更改数据集文件数
     * */
    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSet dst SET " +
            "dst.fileCount = ?1 where dst.datasetId = ?2")
    public void updateDataSetFilecount(long newCount,String datasetId);

    /**
     * 更改数据集上传状态
     * */
    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSet dst SET " +
            "dst.datasetStatus = ?1 where dst.datasetId = ?2")
    public void updateDataSetUploadStatus(String newStatus,String datasetId);

    /**
     * 清空数据集
     * */
    public void clearDataSet(String datasetId);

    /**
     * 删除数据集
     * */
    public void deleteByDataSetId(String datasetId);

    /**
     * 上传文件
     * */
    public void upLoadFiles(String datasetId,List<DataSetFile> files);

    /**
     * 删除文件
     * */
    public void deleteFiles(String datasetId, List<DataSetFile> files);

    /**
     * 数据集排序
     * */
    public void sortDataSet(String datasetId,String sortBy);

}
