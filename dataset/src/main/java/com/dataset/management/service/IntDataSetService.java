package com.dataset.management.service;

import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.List;

/**
 * 数据集功能接口行为；
 */

public interface IntDataSetService {

    /**
     * 保存数据集整个信息
     * */
    public DataSet save(DataSet dataSet);

    /**
     * 定位dataset 方式
     * */
    public DataSet findById(int datasetId);

    public DataSet findByDataSetEnglishNameAndUserId(String datasetEnglishName,int userId);
    public DataSet findByDataSetNameAndUserId(String datasetName,int userId)throws IOException;

    public List<DataSet> findByUserIdAndDataSetNameLike(int userId,String nameLike);
    public List<DataSet> findByUserId(int userId);

    /**
     * 数据集概览
     * */
    public List<DataSet> findAll(Sort sort);

    /**
     * 删除数据集
     * */
    public void deleteById(int datasetId)throws IOException;

    /**
     * 删除文件
     * */
    public void deleteAll();


}
