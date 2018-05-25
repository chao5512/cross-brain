package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.consts.DataSetConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSystem;
import com.dataset.management.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


/**
 * 操作对象 : DataSet
 * 操作依据： datasetId;
 *
 * */
@Controller
@RequestMapping("/dataset")
@CrossOrigin
public class DataSetController {

    private static Logger logger = LoggerFactory.getLogger(DataSetController.class);

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

    //查询
    @ResponseBody
    @RequestMapping(value = {"/listInfo/","/listInfo"},method = RequestMethod.POST)
    public ApiResult listInfoDatasets(@RequestParam(value = "datasetId") String datasetId){
        /**
         * *排序方式已经由之前的 dataSystem  属性设置*
         * sortBY  是不可变的
         * */
        int code =0;
        String message ="展示数据集基本信息表: ";
        logger.info("开始罗列数据据基本信息");
        DataSet dataSet = dataSetService.findByDataSetId(datasetId);
        return new ApiResult(0,dataSet,message);
    }


    /**
     * 修改 ：  由System 移动到 basic 中
     * 修改依据：  datasetId
     *          可以  手选   修改项：
     *
     String en_datasetName,
     String ch_datasetName,
     String path,
     String basicDesc,
     String hivetableName,
     String sortBy,
     String sortType,
     String powerStatus,
     String updateDesc,
     int newMax,
     String dataType,总计   11 个更改

     String dataSetStatus          状态随动，依据数据集文件上传的状态而定
     String dataSetLastUpdateTime  状态随动  伴随更改操作而定；
     int filesCount：              状态随动  依据数据集内文件数量变更；

     *
     * 问题：  数据集英文名称改了 那ID 是不是也因该改
     * */
    @ResponseBody
    @RequestMapping(value = {"/update/","/update"},method = RequestMethod.POST)
    public ApiResult updateDataSet(String datasetId,
                                   @RequestParam(value = "datasetUpdateChnineseName",required = false)String datasetUpdateChnineseName,
                                   @RequestParam(value = "datasetUpdateEnglishName",required = false) String datasetUpdateEnglishName,
                                   @RequestParam(value = "datasetStorePath",required = false) String datasetStorePath,
                                   @RequestParam(value = "dataSetBasicDesc",required = false) String dataSetBasicDesc,
                                   @RequestParam(value = "hiveTbaleName",required = false)String hiveTableName,
                                   @RequestParam(value = "datasetSortBy",required = true)String sortBy,
                                   @RequestParam(value = "datasetSortType") String sortType,
                                   @RequestParam(value = "datasetPower") String dataSetPower,
                                   @RequestParam(value = "datasetUpdateDesc") String dataSetUpdateDesc,
                                   @RequestParam(value = "datasetDtaType") String dataSetDataType,
                                   @RequestParam(value = "maxContener") int maxContener
    )throws IOException {
        int code =0;
        String message = "修改数据集";
        logger.info("准备修改操作，系统表  基本信息表全部准备更新。。。");
        DataSystem dataSystem = dataSetOptService.findByDataSetId(datasetId);
        DataSet dataSet = dataSetService.findByDataSetId(datasetId);
        logger.info("检测到当前数据集系统："+ dataSystem);
        logger.info("检测到当前数据集："+ dataSet);
        if(null == dataSystem && null == dataSet){
            return new ApiResult(-1,null,"未找到系统数据集或者 数据集信息表");
        }
        if(datasetUpdateChnineseName != null){
            dataSystem.setDatasetName(datasetUpdateChnineseName);
            dataSet.setDataSetName(datasetUpdateChnineseName);
        }
        if(datasetUpdateEnglishName != null){
            dataSystem.setDatasetEnglishName(datasetUpdateEnglishName);
            dataSet.setDataSetEnglishName(datasetUpdateEnglishName);
        }
        if(datasetStorePath != null){
            dataSystem.setDatasetStoreurl(datasetStorePath);
            dataSet.setDataSetStoreUrl(datasetStorePath);
        }
        if(dataSetBasicDesc != null){
            dataSystem.setDatasetDesc(dataSetBasicDesc);
            dataSet.setDataSetBasicDesc(dataSetBasicDesc);
        }
        if(hiveTableName != null){
            dataSystem.setDatasetHiveTablename(hiveTableName);
            dataSet.setDataSetHiveTableName(hiveTableName);
        }
        if(sortBy != null){
            dataSystem.setDataSetSystemSortBy(sortBy);
            dataSet.setDataSetSortBY(sortBy);
        }
        if(sortType != null){
            dataSystem.setDataSetSortType(sortType);
            dataSet.setDataSetSortType(sortType);
        }
        if(dataSetPower != null){
            dataSet.setDatasetPower(dataSetPower);
        }
        if(dataSetUpdateDesc != null){
            dataSet.setDataSetUpdateDesc(dataSetUpdateDesc);
        }
        if(dataSetDataType != null){
            dataSet.setDatatype(dataSetDataType);
        }
        if(maxContener > 0){
            dataSet.setMaxContener(maxContener);
        }
        //确认获取新生成的数据
        logger.info("开始更新数据集系统表。。。。。7 ");
        dataSetOptService.update(
                dataSystem.getDatasetEnglishName(),
                dataSystem.getDatasetName(),
                dataSystem.getDatasetStoreurl(),
                dataSystem.getDatasetDesc(),
                dataSystem.getDatasetHiveTablename(),
                dataSystem.getDataSetSystemSortBy(),
                dataSystem.getDataSetSortType(),
                datasetId
        );
        //同时更新 数据集基本表信息
        logger.info("更改对应数据集基本表信息：。。。");
        dataSetService.updateAll(
                dataSet.getDataSetEnglishName(),
                dataSet.getDataSetName(),
                dataSet.getDataSetStoreUrl(),
                dataSet.getDataSetBasicDesc(),
                dataSet.getDataSetHiveTableName(),
                dataSet.getDataSetSortBY(),
                dataSet.getDataSetSortType(),
                dataSet.getDatasetPower(),
                dataSet.getDataSetUpdateDesc(),
                dataSet.getMaxContener(),
                dataSet.getDatatype(),
                datasetId);
        logger.info("重置 数据集更新时间 ");
        dataSet.setContentTimeStamp();
        String newTime = dataSet.getDatasetUpdatetime(dataSet.getContentTimeStamp());
        dataSet.setDatasetUpdatetime(newTime);
        dataSetService.updateDataSetLastUpdateTime(newTime,datasetId);

        //更新hive表名
        try {
            hiveService.changeHiveTable(dataSystem.getDatasetEnglishName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ApiResult(0,dataSystem,"已经更改数据集");
    }

    /**
     * 清空
     *
     * 删除了文件  files   更新 数据集基本表中关于文件的统计   filesCounts
     * */
    @ResponseBody
    @RequestMapping(value = {"/clean/","/clean"},method = RequestMethod.POST)
    public ApiResult cleanDataSet(@RequestParam(value = "datasetId") String datasetId)throws IOException{
        int code = 0;
        String message = "执行清空数据及操作。。。";
        DataSet dataSet = dataSetService.findByDataSetId(datasetId);
        logger.info("获取数据集："+dataSet);
        logger.info("数据集准备清空操作  （删除文件） ");
        dataSetFileService.deleteAll();
        if(dataSetFileService.count() ==0){
            logger.info("数据集当前文件数："+dataSetFileService.count());
            logger.info("远程 hdfs 删除文件中。。");
            hdfsService.setDataSet(dataSet);
            hdfsService.deleteFiles(hdfsService.datasetHdsfFiles());
            int counts = hdfsService.datasetHdsfFiles().size();
            logger.info("hdfs 中 指定路径文件数："+counts);

            dataSet.setFileCount(counts);
            dataSet.setContentTimeStamp();
            String newTime = dataSet.getDatasetUpdatetime(dataSet.getContentTimeStamp());
            String newDesc = "清空了数据集所有文件。。";
            dataSetService.updateDataSetDesc(newDesc,datasetId);
            dataSetService.updateDataSetLastUpdateTime(newTime,datasetId);
            dataSetService.updateDataSetFilecount(counts,datasetId);
            logger.info("数据及基本表中：（文件数）："+dataSet.getFileCount());
            return new ApiResult(0,dataSet,"清空数据及成功");
        }
        return new ApiResult(-1,dataSet,"清空失败");
    }


}
