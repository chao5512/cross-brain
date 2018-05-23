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

    /**
     * 更改数据集多个属性
     * */
    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSet dst SET " +
            "dst.datasetPower = ?1," +
            "dst.sortType = ?2," +
            "dst.datasetUpdatetime =?3," +
            "dst.dataSetUpdateDesc = ?4," +
            "dst.maxContener = ?5," +
            "dst.fileCount = ?6," +
            "dst.datasetStatus = ?7 where dst.datasetId = ?7")
    public void updateAll(String power,
                          String newSortBy,
                          String newTime,
                          String newDesc,
                          int newMax,
                          int newCount,
                          String newStatus,
                          String datasetId);
    /**
     * 更改数据集名称
     * */
    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSet dst SET " +
            "dst.dataSetEngListName = ?1," +
            "dst.dataSetName = ?2 where dst.datasetId = ?3" )
    public void updateDataSetName(String englishName,String chinaName,String dataSetId);

    /**
     * 更改数据集公开状态
     * */
    public void updateDataSetPowerStatus(String datasetId,String powerStatus);

    /**
     * 更改数据集排序方式
     * */
    public void updateDataSetSortBy(String datasetId,String newSortBy);

    /**
     * 更改数据集修改时间
     * */
    public void updateDataSetLastUpdateTime(String datasetId,String newTime);

    /**
     * 更改数据集描述
     * */
    public void updateDataSetDesc(String datasetId,String newDesc);

    /**
     * 更改数据集上限
     * */
    public void updateDataSetMaxContener(String datasetId,int newMax);

    /**
     * 更改数据集文件数
     * */
    public void updateDataSetFilecount(String datasetId,long newCount);

    /**
     * 更改数据集上传状态
     * */
    public void updateDataSetUploadStatus(String datasetId,String newStatus);

    /**
     * 清空数据集
     * */
    public void clearDataSet(String datasetId);

    /**
     * 删除数据集
     * */
    public void deleteDataSet(String datasetId);

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
