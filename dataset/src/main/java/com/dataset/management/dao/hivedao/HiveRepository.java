package com.dataset.management.dao.hivedao;

import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;

import java.io.IOException;

/**
 * @ClassName HiveRepository
 * @Description 操作hive接口
 * @Auther: 王培文
 * @Date: 2018/6/5 
 * @Version 1.0
 **/
public interface HiveRepository {
    boolean createTable(HiveTableMeta tableMeta, User user, DataSet dataSet);
    boolean isExist(DataSet dataSet);
    String getTableNameByDataSet(DataSet dataSet);
    boolean alterTableStructure(HiveTableMeta tableMeta,DataSet dataSet);
    //根据hive表名删除表
    boolean dropTableByName(String name);
}
