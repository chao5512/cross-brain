package com.dataset.management.service;

import com.dataset.management.dao.datasetdao.DataSetRepository;
import com.dataset.management.entity.DataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

    public DataSet findByDataSetNameAndUserId(String datasetName,int userId){
        return dataSetRepository.findByDataSetNameAndUserId(datasetName,userId);
    }
    public DataSet findByDataSetEnglishNameAndUserId(String datasetEnglishName,int userId){
        return dataSetRepository.findByDataSetEnglishNameAndUserId(datasetEnglishName,userId);
    }

    public List<DataSet> findByUserIdAndDataSetNameLike(int userId,String nameLike){
        return dataSetRepository.findByUserIdAndDataSetNameLike(userId,nameLike);
    }
    public List<DataSet> findByUserId(int userId){
        return dataSetRepository.findByUserId(userId);
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
    @Override
    public void deleteById(int datasetId)throws IOException{
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
