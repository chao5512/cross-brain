package com.dataset.management.service.impl;

import com.dataset.management.Dao.DataSetMetastoreRepository;
import com.dataset.management.Dao.HiveRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.service.DataSetMetastoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSetMetastoreServiceImpl implements DataSetMetastoreService {
    @Autowired
    private HiveRepository hiveRepository;
    @Autowired
    private DataSetMetastoreRepository metastoreRepository;
    @Override
    public HiveTableMeta getHiveTableMeta(DataSet dataSet) {
        String tableName = hiveRepository.getTableNameByDataSet(dataSet);
        HiveTableMeta hiveTableMeta = metastoreRepository.getHiveTableMeta(tableName);
        return hiveTableMeta;
    }
}
