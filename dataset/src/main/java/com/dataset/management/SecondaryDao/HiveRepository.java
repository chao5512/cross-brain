package com.dataset.management.SecondaryDao;

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
    void createTable(HiveTableMeta tableMeta, User user, DataSet dataSet) throws IOException;
    boolean isExist(DataSet dataSet);
    String getTableNameByDataSet(DataSet dataSet);
    boolean alterTableStructure(HiveTableMeta tableMeta,DataSet dataSet);
}
