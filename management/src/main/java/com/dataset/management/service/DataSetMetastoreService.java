package com.dataset.management.service;

import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;

public interface DataSetMetastoreService {
    HiveTableMeta getHiveTableMeta(DataSet dataSet);
}
