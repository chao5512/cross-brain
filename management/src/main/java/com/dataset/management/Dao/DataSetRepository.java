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
    public DataSet findById(int datasetId);

    public DataSet findByDataSetName (String datasetName);

    public List<DataSet> findByUserId(int userid);
    public List<DataSet> findByUserName(String userName);

    /**
     * 数据集概览
     * */
    public List<DataSet> findAll(Sort sort);

    /**
     * 删除数据集
     * */
    public void deleteById(int datasetId);

    /**
     * 删除文件
     * */
    public void deleteAll();


}
