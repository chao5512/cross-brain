package com.dataset.management.PrimaryDao;

import com.dataset.management.entity.DataSet;
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
    public DataSet findByDataSetEnglishName (String datasetName);
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
