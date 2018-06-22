package com.dataset.management;

import com.dataset.management.SecondaryDao.DataSetMetastoreRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.service.HiveTableService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MetaData {
    @Resource
    DataSetMetastoreRepository repository;

    @Resource
    HiveTableService tableService;
    @Test
    public void test(){
        boolean existLineDelim = repository.isExistLineDelim("203_titanic_orc");
        System.out.println(existLineDelim);
    }

    @Test
    public void test01(){
        HiveTableMeta hiveTableMeta = repository.getHiveTableMeta("studentno");
        System.out.println("列分隔符："+hiveTableMeta.getFieldDelim());
        System.out.println("行分隔符："+hiveTableMeta.getLineDelim());
    }
    @Test
    public void test02(){
        DataSet dataSet = new DataSet();
        dataSet.setId(203);
        String tableName = tableService.getTableNameByDataSet(dataSet);
        int index = tableName.indexOf("_");
        int length = tableName.length();
        String subTableName = tableName.substring(index + 1, length);
        System.out.println(subTableName);
    }

}
