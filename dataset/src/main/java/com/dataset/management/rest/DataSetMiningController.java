package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.service.DataSetMetastoreService;
import com.dataset.management.service.HiveTableService;
import com.dataset.management.service.IntDataSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName DataSetMiningController
 * @Description 数据探索
 * @Auther: 王培文
 * @Date: 2018/6/21 
 * @Version 1.0
 **/
@RestController
@RequestMapping(value = "mining")
@CrossOrigin
public class DataSetMiningController {

    private static Logger logger = LoggerFactory.getLogger(DataSetMiningController.class);

    @Autowired
    IntDataSetService dataSetService;

    @Autowired
    DataSetMetastoreService metastoreService;

    @Autowired
    HiveTableService tableService;


    /**
     * 功能描述:根据用户名查询数据集名字
     * @param userName
     * @return: com.dataset.management.common.ApiResult
     * @auther: 王培文
     * @date: 2018/6/21 15:22
     */
    @RequestMapping(value = "dataSetTableName")
    public ApiResult getDataSetName(@RequestParam("userName") String userName){
        logger.info("执行了。。。。。。。。");
        List<DataSet> dataSets = dataSetService.findByUserName(userName);
        List<String> dataSetTableNameList = new ArrayList<>();
        for (DataSet dataSet:dataSets) {
            String tableName = tableService.getTableNameByDataSet(dataSet);
            if(tableName != null){
                dataSetTableNameList.add(tableName);
            }
        }
        return ResultUtil.success(dataSetTableNameList);
    }

    /**
     * 功能描述:根据表名查询表字段信息
     * @param tableName
     * @return: com.dataset.management.common.ApiResult
     * @auther: 王培文
     * @date: 2018/6/21 15:23
     */
    @RequestMapping("dataSetColumns")
    public ApiResult getDataSetColumns(@RequestParam("tableName") String tableName){
        /*int selectedDataSetId=0;
        List<DataSet> dataSets = dataSetService.findByUserName(userName);
        for (DataSet dataSet:dataSets) {
            String setName = dataSet.getDataSetName();
            if (dataSetName.equals(setName)){
                selectedDataSetId = dataSet.getId();
            }
        }
        logger.info("数据集id："+selectedDataSetId);
        DataSet dataSet = new DataSet();
        dataSet.setId(selectedDataSetId);
        HiveTableMeta tableMeta = metastoreService.getHiveTableMeta(dataSet);*/
        HiveTableMeta tableMeta = metastoreService.getHiveTableMeta(tableName);
        List<FieldMeta> fields = tableMeta.getFields();
        //fieldNameList 存放查询出来的表字段信息
        List<String> fieldNameList = new ArrayList<String>();
        for (FieldMeta field:fields) {
            String fieldName = field.getFieldName();
            fieldNameList.add(fieldName);
        }
        return  ResultUtil.success(fieldNameList);
    }
}
