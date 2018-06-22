package com.dataset.management;

import com.dataset.management.SecondaryDao.DataSetMetastoreRepository;
import com.dataset.management.entity.HiveTableMeta;
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

}
