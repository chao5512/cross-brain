package com.dataset.management.service;

import com.dataset.management.Dao.DataSetRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DataSetService implements IntDataSetService {

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

    public DataSet findFirst(){
      return   dataSetRepository.findFirst();
    }


    /**
     * 更改数据集多个属性
     * */
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
                          String datasetId){}

    /**
     * 更改数据集公开状态
     * */
    public void updateDataSetPowerStatus(String powerStatus,String datasetId){
        dataSetRepository.updateDataSetPowerStatus(powerStatus,datasetId);
    };

    /**
     * 更改数据集排序方式
     * */
    public void updateDataSetSortBy(String newSortBy){
        dataSetRepository.updateDataSetSortBy(newSortBy);
    }
    public void updateDataSetSortType(String sortType){

    }

    /**
     * 更改数据集修改时间
     * */
    public void updateDataSetLastUpdateTime(String newTime,String datasetId){
        dataSetRepository.updateDataSetLastUpdateTime(newTime,datasetId);
    }
    /**
     * 更改数据集描述
     * */
    public void updateDataSetDesc(String newDesc,String datasetId){}

    /**
     * 更改数据集上限
     * */
    public void updateDataSetMaxContener(int newMax,String datasetId){
        dataSetRepository.updateDataSetMaxContener(newMax,datasetId);
    }

    /**
     * 更改数据集文件数
     * */
    public void updateDataSetFilecount(long newCount,String datasetId){}

    /**
     * 更改数据集上传状态
     * */
    public void updateDataSetUploadStatus(String newStatus,String datasetId){}

    /**
     * 清空数据集
     * */
    @Override
    public void clearDataSet(String datasetId){

    };

    /**
     * 删除数据集
     * */
    public void deleteByDataSetId(String datasetId){
        dataSetRepository.deleteByDataSetId(datasetId);
    };

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

    /**
     * 更改数据集名称
     * */
    public void updateDataSetName(String englishName,String chinaName,String sortBy,String dataSetId){};


}
