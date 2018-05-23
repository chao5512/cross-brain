package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.entity.DataSystem;
import com.dataset.management.entity.Hiveinfo;
import com.dataset.management.service.*;

import org.apache.hadoop.hive.common.LogUtils;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/dataset")
@CrossOrigin
public class DataSetController {
/***
 * 空值最外面的系统数据集的展示和操作
 *
 * **/
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


    private static final ExecutorService exeService = Executors.newFixedThreadPool(5);

    @ResponseBody
    @RequestMapping(value = {"/create/","/cretae"},method = RequestMethod.POST)
    public ApiResult createDataSet(DataSet dataSet) throws IOException{
        int code  = 0;
        String message = "begin create dataset";
        if(null == dataSet){
            return new ApiResult(code,null,"The DataSet must not null");
        }
        logger.info("DataSetParams: "+ JSON.toString(dataSet));

        DataSystem newDataSystem = packageDataSystem(dataSet);
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
                    hiveService.createHiveTable();
                    logger.info("hive表生成完毕。。。。。");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return  new ApiResult(code,newDataSystem,message);
    }
    //查询
    @ResponseBody
    @RequestMapping(value = {"/select/","/select"},method = RequestMethod.POST)
    public ApiResult selectDataSet(@RequestParam(value = "dataSetSystemSortBy") String sortBy,
                                   DataSystem dataSystem) throws IOException{
        int code =0;
        String message = "查询数据集系统表中。。。。";
        //默认根据英文名字排序
        if(sortBy != null){
            logger.info("排序方式："+sortBy);
            Sort sort = changSortBy("desc",sortBy);
            // list<DataSystem>
            dataSetOptService.findAll(sort);
            message ="已经按照"+sortBy+" 方式排序";
        }else {
            logger.info("mr排序");
            Sort sort = basicSortBy();
            dataSetOptService.findAll(sort);
            message ="已经按照默认方式排序";
        }
        return new ApiResult(0,dataSystem,message);
    }
    /**
     *  修改
     *
     *  在system 和 datasetbasic  表中修改，同时由 datasetId 获取，同时更改各自允许修改的 字段 ；
     * */
    @ResponseBody
    @RequestMapping(value = {"/update/","/update"},method = RequestMethod.POST)
    public ApiResult updateDataSet(@RequestParam(value = "datasetId") String datasetId,
                                   @RequestParam(value = "datasetUpdateChnineseName",required = false)String datasetUpdateChnineseName,
                                   @RequestParam(value = "datasetUpdateEnglishName",required = false) String datasetUpdateEnglishName,
                                   @RequestParam(value = "datasetStorePath",required = false) String datasetStorePath,
                                   @RequestParam(value = "datatsetUpdateDesc",required = false) String datatsetUpdateDesc,
                                   @RequestParam(value = "hiveTbaleName",required = false)String hiveTableName
                                   )throws IOException{
        int code =0;
        String message = "修改系统中的数据集。。。。。";
        DataSystem dataSystem = dataSetOptService.findByDataSetId(datasetId);
        DataSet dataSet = dataSetService.findByDataSetId(datasetId);
        logger.info("检测到当前数据集："+ dataSystem);
        if(null == dataSystem){
            return new ApiResult(-1,dataSystem,"未找到数据集");
        }
        if(datasetUpdateChnineseName != null){
            dataSystem.setDatasetName(datasetUpdateChnineseName);
            dataSet.setDataSetName(datasetUpdateChnineseName);
        }
        if(datasetUpdateEnglishName != null){
            dataSystem.setDatasetEnglishName(datasetUpdateEnglishName);
            dataSet.setDataSetEngListName(datasetUpdateEnglishName);
        }
        if(datasetStorePath != null){
            dataSystem.setDatasetStoreurl(datasetStorePath);
            dataSet.setDataSetStoreUrl(datasetStorePath);
        }
        if(datatsetUpdateDesc != null){
            dataSystem.setDatasetDesc(datatsetUpdateDesc);
            dataSet.setDataSetBasicDesc(datatsetUpdateDesc);
        }
        if(hiveTableName != null){
            dataSystem.setDatasetHiveTablename(hiveTableName);
            dataSet.setDataSetHiveTableName(hiveTableName);
        }
        //确认获取新生成的数据
        logger.info("开始数据集系统表。。。。。7 ");
        dataSetOptService.update(
                dataSystem.getDatasetEnglishName(),
                dataSystem.getDatasetName(),
                dataSystem.getDatasetStoreurl(),
                dataSystem.getDatasetDesc(),
                dataSystem.getDatasetHiveTablename(),
                dataSystem.getDataSetId()
                );
        //同时更新 数据集基本表信息
        logger.info("更改对应数据集基本表信息：。。。");
        dataSetService.updateDataSetName(dataSystem.getDatasetEnglishName(),dataSystem.getDatasetName(),dataSystem.getDataSetId());
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
    @RequestMapping(value = {"/update/","/update"},method = RequestMethod.POST)
    public ApiResult updateDataSet(@RequestParam(value = "datasetId") String datasetId)throws IOException{
        int code = 0;
        String message = "执行清空数据及操作。。。";
        DataSet dataSet = dataSetService.findByDataSetId(datasetId);
        logger.info("获取数据集："+dataSet);
        logger.info("数据集准备清空操作  （删除文件） ");
        dataSetFileService.deleteAll();
        logger.info("数据集当前文件数："+dataSetFileService.count());
        if(dataSetFileService.count() ==0){
            dataSet.setFileCount(0);
            logger.info("远程 hdfs 删除文件中。。");
            hdfsService.deleteFiles(hdfsService.datasetHdsfFiles());
            int counts = hdfsService.datasetHdsfFiles().size();
            logger.info("hdfs 中 指定路径文件数："+counts);
            dataSet.setFileCount(counts);
            dataSetService.updateDataSetFilecount(datasetId,counts);
            logger.info("数据及基本表中：（文件数）："+dataSet.getFileCount());
            return new ApiResult(0,dataSet,"清空数据及成功");
        }
        return new ApiResult(-1,dataSet,"清空失败");
    }
    //删除


    private DataSystem packageDataSystem(DataSet dataSet){
        DataSystem dataSystem = new DataSystem();
        dataSystem.setDataSetId(dataSet.getDatasetId());
        dataSystem.setDatasetName(dataSet.getDataSetName());
        dataSystem.setDataSetUserId(dataSet.getDatasetOwner());
        dataSystem.setDatasetDesc(dataSet.getDataSetBasicDesc());
        dataSystem.setDatasetHiveTablename(dataSet.getDataSetHiveTableName());
        dataSystem.setDataSetHiveTableId(dataSet.getDataSetHiveTableId());
        dataSystem.setDatasetCreateDate(dataSet.getDataSetCreateTime());
        dataSystem.setDatasetEnglishName(dataSet.getDataSetEngListName());
        return dataSystem;
    }

    private Sort changSortBy(String orderType,String orderColumn){
        Sort sort = new Sort(Sort.Direction.fromString(orderType),orderColumn);
        return sort;
    }

    private Sort basicSortBy(){
        return changSortBy("desc","dataSetEngListName");
    }



}
