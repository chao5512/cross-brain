package com.dataset.management;

import com.dataset.management.SecondaryDao.DataSetMetastoreRepository;
import com.dataset.management.SecondaryDao.HiveRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.service.DataSetMetastoreService;
import com.dataset.management.service.HiveTableService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MetaData {
    @Resource
    DataSetMetastoreRepository repository;

    @Autowired
    private DataSetMetastoreService metastoreService;

    @Resource
    HiveTableService tableService;

    @Autowired
    HiveRepository hiveRepository;
    /*@Test
    public void test(){
        boolean existLineDelim = repository.isExistLineDelim("203_titanic_orc");
        System.out.println(existLineDelim);
    }*/

   /* @Test
    public void test03(){
        *//*DataSet dataSet = new DataSet();
        dataSet.setId(203);
        String tableNameByDataSet = hiveRepository.getTableNameByDataSet(dataSet);
        System.out.println(tableNameByDataSet);*//*
        DataSet dataSet = new DataSet();
        dataSet.setId(203);
        HiveTableMeta hiveTableMeta = metastoreService.getHiveTableMeta(dataSet);
        System.out.println(hiveTableMeta.getTableName());
        List<FieldMeta> fields = hiveTableMeta.getFields();
        for (FieldMeta field:fields) {
            System.out.println(field.getFieldComment());
        }
    }*/


    @Test
    public void test01(){
        /*HiveTableMeta hiveTableMeta1 = repository.getHiveTableMeta("203_titanic_orc01");
        System.out.println("存储类型1："+hiveTableMeta1.getFiletype());
        HiveTableMeta hiveTableMeta2 = repository.getHiveTableMeta("203_titanic_orc02");
        System.out.println("存储类型2："+hiveTableMeta2.getFiletype());
        HiveTableMeta hiveTableMeta3 = repository.getHiveTableMeta("203_titanic_orc03");
        System.out.println("存储类型3："+hiveTableMeta3.getFiletype());
        HiveTableMeta hiveTableMeta4 = repository.getHiveTableMeta("203_titanic_orc04");
        System.out.println("存储类型4："+hiveTableMeta4.getFiletype());*/
//        System.out.println("列分隔符："+hiveTableMeta.getFieldDelim());
//        System.out.println("行分隔符："+hiveTableMeta.getLineDelim());
    }
    /*@Test
    public void test02(){
        DataSet dataSet = new DataSet();
        dataSet.setId(203);
        String tableName = tableService.getTableNameByDataSet(dataSet);
        int index = tableName.indexOf("_");
        int length = tableName.length();
        String subTableName = tableName.substring(index + 1, length);
        System.out.println(subTableName);
    }*/

}
