package com.dataset.management.Dao;

import com.dataset.management.entity.HiveTableMeta;

public interface DataSetMetastoreRepository {
    HiveTableMeta getHiveTableMeta(String tableName);
}
