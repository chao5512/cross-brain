package com.dataset.management.service;

import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;

import java.io.IOException;

/**
 * @ClassName HiveTableService
 * @Description 操作hive接口
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
public interface HiveTableService {
    boolean createTable(HiveTableMeta tableMeta, User user, DataSet dataSet) throws IOException;
    boolean alterTableStructure(HiveTableMeta tableMeta,User user,DataSet dataSet);
    boolean isExist(DataSet dataSet);
    String getTableNameByDataSet(DataSet dataSet);
}
