package com.dataset.management.service;

import com.dataset.management.PrimaryDao.DataSetRepository;
import com.dataset.management.entity.DataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class DataSetService implements IntDataSetService {

    @Autowired
    DataSetRepository dataSetRepository;
    /**
     * 保存数据集整个信息   修改也在此
     * */
    @Override
    public DataSet save(DataSet dataSet){
        return  dataSetRepository.save(dataSet);
    }

    /**
     * 定位dataset 方式
     * */
    public DataSet findById(int datasetId){
        return dataSetRepository.findById(datasetId);
    }

    public DataSet findByDataSetEnglishName(String datasetName){
        return dataSetRepository.findByDataSetEnglishName(datasetName);
    }
    public List<DataSet> findByUserName(String userName){
        return dataSetRepository.findByUserName(userName);
    }

    /**
     * 数据集概览
     * */
    public List<DataSet> findAll(Sort sort){
        return dataSetRepository.findAll(sort);
    }


    /**
     * 删除数据集
     * */
    @Transactional
    public void deleteById(int datasetId){
        dataSetRepository.deleteById(datasetId);
    }

    /**
     * 删除文件
     * */
    @Transactional
    public void deleteAll(){
        dataSetRepository.deleteAll();
    }

}
