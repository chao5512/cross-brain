package com.dataset.management.service;

import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;

/**
 * @ClassName DataSetMetastoreService
 * @Description 操作元数据mysql接口
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
public interface DataSetMetastoreService {
    HiveTableMeta getHiveTableMeta(DataSet dataSet);
    HiveTableMeta getHiveTableMeta(String tableName);
}
