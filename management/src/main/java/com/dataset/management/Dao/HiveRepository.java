package com.dataset.management.Dao;

import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;

public interface HiveRepository {
    void createTable(HiveTableMeta tableMeta, User user, DataSet dataSet);
    boolean isExist(DataSet dataSet);
    String getTableNameByDataSet(DataSet dataSet);
}
