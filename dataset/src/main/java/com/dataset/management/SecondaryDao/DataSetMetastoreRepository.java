package com.dataset.management.SecondaryDao;

import com.dataset.management.entity.HiveTableMeta;

/**
 * @ClassName DataSetMetastoreRepository
 * @Description 操作元数据msyql接口
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
public interface DataSetMetastoreRepository {
    HiveTableMeta getHiveTableMeta(String tableName);
}
