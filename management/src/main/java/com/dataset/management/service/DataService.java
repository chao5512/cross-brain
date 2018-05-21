package com.dataset.management.service;

import com.dataset.management.Dao.DataSetRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;

public class DataService implements IntDataSetService {
    @Autowired
    DataSetRepository dataSetRepository;
    /**
     * 保存数据集整个信息
     * */
    @Override
    public DataSet save(DataSet dataSet){
        return  dataSetRepository.save(dataSet);
    }

    /**
     * 定位dataset 方式
     * */
    public DataSet findByDataSetId(String datasetId){
        return dataSetRepository.findByDataSetId(datasetId);
    }
    public DataSet findByDataSetName(String datasetName){
        return dataSetRepository.findByDataSetName(datasetName);
    }

    /**
     * 数据集概览
     * */
    public List<DataSet> findAll(Sort sort){
        return dataSetRepository.findAll(sort);
    }

    /**
     * 更改数据集多个属性
     * */
    public void updateAll(String powerStatus,
                          String newSortBy,
                          String newTime,
                          String newDesc,
                          int newMax,
                          int newCount,
                          String newStatus,
                          String datasetId){}

    /**
     * 更改数据集公开状态
     * */
    public void updateDataSetPowerStatus(String datasetId,String powerStatus){};

    /**
     * 更改数据集排序方式
     * */
    public void updateDataSetSortBy(String datasetId,String newSortBy){}

    /**
     * 更改数据集修改时间
     * */
    public void updateDataSetLastUpdateTime(String datasetId,String newTime){

    }

    /**
     * 更改数据集描述
     * */
    public void updateDataSetDesc(String datasetId,String newDesc){}

    /**
     * 更改数据集上限
     * */
    public void updateDataSetMaxContener(String datasetId,int newMax){}

    /**
     * 更改数据集文件数
     * */
    public void updateDataSetFilecount(String datasetId,long newCount){}

    /**
     * 更改数据集上传状态
     * */
    public void updateDataSetUploadStatus(String datasetId,String newStatus){}

    /**
     * 清空数据集
     * */
    @Override
    public void clearDataSet(String datasetId){

    };

    /**
     * 删除数据集
     * */
    public void deleteDataSet(String datasetId){};

    /**
     * 上传文件
     * */
    public void upLoadFiles(String datasetId,List<DataSetFile> files){};

    /**
     * 删除文件
     * */
    public void deleteFiles(String datasetId, List<DataSetFile> files){};

    /**
     * 数据集排序
     * */
    public void sortDataSet(String datasetId,String sortBy){};


}
