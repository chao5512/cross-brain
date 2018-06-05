package com.dataset.management.service.impl;

import com.dataset.management.Dao.DataSetMetastoreRepository;
import com.dataset.management.Dao.HiveRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.service.DataSetMetastoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName DataSetMetastoreServiceImpl
 * @Description 操作元数据mysql实体类
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
@Service
public class DataSetMetastoreServiceImpl implements DataSetMetastoreService {
    @Autowired
    private HiveRepository hiveRepository;
    @Autowired
    private DataSetMetastoreRepository metastoreRepository;

    /**
     * 功能描述:获取表的元数据信息
     * @param dataSet
     * @return: com.dataset.management.entity.HiveTableMeta
     * @auther: 王培文
     * @date: 2018/6/5 16:03
     */
    @Override
    public HiveTableMeta getHiveTableMeta(DataSet dataSet) {
        String tableName = hiveRepository.getTableNameByDataSet(dataSet);
        HiveTableMeta hiveTableMeta = metastoreRepository.getHiveTableMeta(tableName);
        return hiveTableMeta;
    }
}
