package com.dataset.management;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dataset.management.dao.hivedao.DataSetMetastoreRepository;
import com.dataset.management.dao.hivedao.HiveRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.service.DataSetMetastoreService;
import com.dataset.management.service.HiveTableService;
import com.dataset.management.util.JedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


    @Autowired
    private JedisUtils jedisUtils;

    @Test
    public void test05(){
        /*String sign = "aaabbbPreventRepeat";
        System.out.println(sign.endsWith("PreventRepeat"));*/
        HiveTableMeta tableMeta = new HiveTableMeta();
        tableMeta.setTableName("193_titanic");
        tableMeta.setTableComment("泰坦尼克");
        tableMeta.setFiletype("orc");
        tableMeta.setLineDelim("\n");
        tableMeta.setFieldDelim("\t");
        FieldMeta fieldMeta = new FieldMeta();
        fieldMeta.setFieldName("name");
        fieldMeta.setFieldType("string");
        fieldMeta.setFieldComment("名字");
        FieldMeta fieldMeta1 = new FieldMeta();
        fieldMeta1.setFieldName("age");
        fieldMeta1.setFieldType("int");
        fieldMeta1.setFieldComment("年龄");
        List<FieldMeta> fieldMetas = new ArrayList<FieldMeta>();
        fieldMetas.add(fieldMeta);
        fieldMetas.add(fieldMeta1);
        tableMeta.setFields(fieldMetas);
        Map<String, Object> map = new HashMap<String ,Object>();
        map.put("tableMeta",tableMeta);
        map.put("userId",193);
        map.put("dataSetId",150);
        map.put("token",123123);
        String s = JSON.toJSONString(map);
        System.out.println(s);

        //解析json
        JSONObject jsonObject = JSONObject.parseObject(s);
        String userId = jsonObject.getString("userId");
        System.out.println("userId: " + userId);
        HiveTableMeta tableMeta1 = jsonObject.getObject("tableMeta", HiveTableMeta.class);
        System.out.println("表名："+tableMeta1.getTableName());
        System.out.println("表注释："+tableMeta1.getTableComment());
        System.out.println("行分隔符："+tableMeta1.getLineDelim());
        System.out.println("列分隔符："+tableMeta1.getFieldDelim());
        System.out.println("文件类型："+tableMeta1.getFiletype());
        List<FieldMeta> fields = tableMeta1.getFields();
        for (FieldMeta field:fields) {
            System.out.println("字段名："+field.getFieldName());
            System.out.println("字段类型："+field.getFieldType());
            System.out.println("字段注释："+field.getFieldComment());
        }
    }

    @Test
    public void test(){
        HiveTableMeta hiveTableMeta = repository.getHiveTableMeta("193_stuno");
        System.out.println(hiveTableMeta);
        System.out.println(hiveTableMeta.getTableName());
        List<FieldMeta> fields = hiveTableMeta.getFields();
        for (FieldMeta field:fields) {
            System.out.println(field.getFieldName());
        }
        /*try {
            DataSystem byDataSetId = dataSetOptService.findByDataSetId(502);
            System.out.println(byDataSetId.getDatasetStoreurl());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*boolean existLineDelim = repository.isExistLineDelim("203_titanic_orc");
        System.out.println(existLineDelim);*/
    }

    @Test
    public void test04(){
        boolean b = jedisUtils.tryGetDistributedLock("aaa", "bbb", 60000);
        System.out.println(b);
    }

    @Test
    public void test03(){
        /*DataSet dataSet = new DataSet();
        dataSet.setId(203);
        String tableNameByDataSet = hiveRepository.getTableNameByDataSet(dataSet);
        System.out.println(tableNameByDataSet);*/
        DataSet dataSet = new DataSet();
        dataSet.setId(93);
        HiveTableMeta hiveTableMeta = metastoreService.getHiveTableMeta(dataSet);
        System.out.println(hiveTableMeta.getTableName());
        List<FieldMeta> fields = hiveTableMeta.getFields();
        for (FieldMeta field:fields) {
            System.out.println(field.getFieldComment());
        }
    }


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
