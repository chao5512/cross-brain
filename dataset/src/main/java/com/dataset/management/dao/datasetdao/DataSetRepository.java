package com.dataset.management.dao.datasetdao;

import com.dataset.management.entity.DataSet;

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSetRepository extends JpaRepository<DataSet,String> {
    /**
     * 保存数据集整个信息
     * */
    public DataSet save(DataSet dataSet);

    /**
     * 定位dataset 方式
     * */
    public DataSet findById(int datasetId);
    public DataSet findByDataSetNameAndUserId(String datasetName,int userId);
    public DataSet findByDataSetEnglishNameAndUserId(String datasetEnglishName,int userId);
    public List<DataSet> findByUserIdAndDataSetNameContaining(int userId,String nameLike);
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
     * 删除全部
     * */
    public void deleteAll();

}
