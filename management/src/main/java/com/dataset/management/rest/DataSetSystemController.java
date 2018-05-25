package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.consts.DataSetConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.entity.DataSystem;
import com.dataset.management.entity.Hiveinfo;
import com.dataset.management.service.*;

import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
@Controller
@RequestMapping("/dataset")
@CrossOrigin
public class DataSetSystemController {
/***
 * 空值最外面的系统数据集的展示和操作
 *
 * **/
    private static Logger logger = LoggerFactory.getLogger(DataSetSystemController.class);

    @Autowired
    IntDataSetOptService dataSetOptService;

    @Autowired
    IntDataSetService dataSetService;

    @Autowired
    IntDataSetFileService dataSetFileService;

    @Autowired
    DataSetColumService dataSetColumService;

    @Autowired
    HiveService hiveService;

    @Autowired
    HdfsService hdfsService;

    private static DataSystem newDataSystem = new DataSystem();

    private static final ExecutorService exeService = Executors.newFixedThreadPool(5);

    @ResponseBody
    @RequestMapping(value = {"/create/","/cretae"},method = RequestMethod.POST)
    public ApiResult createDataSet(DataSet dataSet) throws IOException{
        int code  = 0;
        String message = "begin create dataset";
        if(null == dataSet){
            return  ResultUtil.error(-1,"无法获取有效的 dataset 属性");
        }
        logger.info("DataSetParams: "+ JSON.toString(dataSet));

        newDataSystem = packageDataSystem(dataSet);
        //体统表信息
        dataSetOptService.save(newDataSystem);
        logger.info("数据集系统表创建：。。。。。");
        exeService.submit(new Runnable() {
            @Override
            public void run() {
                //数据集表信息
                dataSetService.save(dataSet);
                logger.info("数据集基本表创建。。。。。。");
                /**
                 * hive info 如何放入？？？
                 * */
                Hiveinfo hiveinfo = dataSet.getHiveinfo();
                hiveService.setHiveinfo(hiveinfo);
                logger.info("获取hive表  设计结构："+hiveinfo);
                try {
                    logger.info("生成hvie 表中。。。。。");
                    hiveService.createDataBase();
                    hiveService.createHiveTable(dataSet.getDataSetEnglishName());
                    logger.info("hive表生成完毕。。。。。");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return  ResultUtil.success();
    }
    //查询
    @ResponseBody
    @RequestMapping(value = {"/select/","/selectAll"},method = RequestMethod.POST)
    public ApiResult selectAllDataSet(@RequestParam(value = "dataSetSystemSortBy") String sortBy,String sortType) throws IOException{
        int code =0;
        String message = "查询数据集系统表中。。。。";
        Sort sort;
        //默认根据英文名字排序
        if(sortBy != null && sortType != null){
            logger.info("排序方式："+sortBy);
            sort = changSortBy(sortType,sortBy);
            // list<DataSystem>
            message ="已经按照"+sortBy+" 方式排序";
        }else {
            logger.info("默认排序");
            sort = basicSortBy();
            message ="已经按照默认方式排序";
        }
        List<DataSystem> dataSystemList = dataSetOptService.findAll(sort);
        dataSetService.updateDataSetSortBy(sortBy);
        dataSetService.updateDataSetSortType(sortType);
        return ResultUtil.success(dataSystemList);
    }

    /**
     *删除
     *
     *数据集系统表删除，数据集基本表信息删除，删除数据集关于文件的信息，删除数据集hdfs 文件  删除 hive 表
     */

    @ResponseBody
    @RequestMapping(value = {"/delete/","/delete"},method = RequestMethod.POST)
    public ApiResult deleteDataSet(@RequestParam(value = "datasetId") String dataSetId)throws IOException{
        int code = 0;
        String message = "准备删除数据集。。。";
        DataSystem dataSystem = dataSetOptService.findByDataSetId(dataSetId);
        logger.info("获取系统表数据集："+dataSystem);
        logger.info("准备从系统表中删除数据集：");
        dataSetOptService.deleteByDataSetId(dataSetId);

        DataSet dataSet = dataSetService.findByDataSetId(dataSetId);
        logger.info("获取数据集基本信息表："+dataSet);
        logger.info("准备从数据集基本信息表中删除数据集：");
        dataSetService.deleteByDataSetId(dataSetId);

        logger.info("获取数据集文件信息表：");
        logger.info("准备从数据集文件表中删除数据集：");
        dataSetFileService.deleteBydatasetId(dataSetId);

        try {
            logger.info("准备删除 hive 表"+hiveService.getHiveTableName());
            hiveService.dropHiveTable(hiveService.getHiveTableName());
            logger.info("准备删除 hdfs中 数据集文件");
            hdfsService.setDataSet(dataSet);
            hdfsService.deleteFiles(hdfsService.datasetHdsfFiles());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtil.success();
    }


    private DataSystem packageDataSystem(DataSet dataSet){
        DataSystem dataSystem = new DataSystem();
        dataSystem.setDatasetName(dataSet.getDataSetName());
        dataSystem.setDatasetEnglishName(dataSet.getDataSetEnglishName());
        dataSystem.setDataSetId(dataSet.getDatasetId());
        dataSystem.setDatasetCreateDate(dataSet.getDataSetCreateTime());
        dataSystem.setDatasetStoreurl(dataSet.getDataSetStoreUrl());
        dataSystem.setDatasetDesc(dataSet.getDataSetBasicDesc());
        dataSystem.setDatasetHiveTablename(dataSet.getDataSetHiveTableName());
        dataSystem.setDataSetHiveTableId(dataSet.getDataSetHiveTableId());
//        dataSystem.setDataSetUserId(dataSet.getDatasetOwner());
        dataSystem.setDataSetSystemSortBy(dataSet.getDataSetSortBY());
        dataSystem.setDataSetSortType(dataSet.getDataSetSortType());
        return dataSystem;
    }

    private Sort changSortBy(String orderType,String orderColumn){
        Sort sort = new Sort(Sort.Direction.fromString(orderType),orderColumn);
        return sort;
    }

    private Sort basicSortBy(){
        return changSortBy(DataSetConsts.SORTTYPE_DESC,DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME);
    }



}
