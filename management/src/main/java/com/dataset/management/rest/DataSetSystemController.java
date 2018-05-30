package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.consts.DataSetConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.entity.DataSystem;
import com.dataset.management.service.*;

import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
@Controller
@RequestMapping("datasetSystem")
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

//    @Autowired
//    DataSetColumService dataSetColumService;
//
//    @Autowired
//    HiveService hiveService;
//
//    @Autowired
//    HdfsService hdfsService;

    private static final ExecutorService exeService = Executors.newFixedThreadPool(5);

    @ResponseBody
    @RequestMapping(value = {"/create/"},method = RequestMethod.POST)
    public ApiResult createDataSet() throws IOException{
        int code  = 0;
        String message = "begin create dataset";
        //先生成默认的
        DataSet dataSet = packageDataSet();
        String newDataSetName = dataSet.getDataSetName();
        DataSet existsDataSet = dataSetService.findByDataSetName(newDataSetName);
        if(existsDataSet!= null ){
            return  ResultUtil.error(-1,"数据集已经存在，无法重新创建，请重新定义数据集名称，或者修改已经存在数据集信息");
        }
        logger.info("准备创建的数据集: "+ JSON.toString(dataSet));

        DataSystem newDataSystem = packageDataSystem(dataSet);
        //体统表信息
        logger.info("数据集系统表开始创建：。。。。。");
        dataSetOptService.save(newDataSystem);

        exeService.submit(new Runnable() {
            @Override
            public void run() {
                //数据集表信息
                logger.info("数据集基本表创建。。。。。。");
                dataSetService.save(dataSet);
                /**
                 * hive info 如何放入？？？
                 * */
//                Hiveinfo hiveinfo = dataSet.getHiveinfo();
//                hiveService.setHiveinfo(hiveinfo);
//                logger.info("获取hive表  设计结构："+hiveinfo);
                try {
                    logger.info("生成hvie 表中。。。。。");
//                    hiveService.createDataBase();
//                    hiveService.createHiveTable(dataSet.getDataSetEnglishName());
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
    @RequestMapping(value = {"/selectAll/{dataSetSystemSortBy},{dataSetSystemSortType}"},method = RequestMethod.GET)
    public ApiResult selectAllDataSet(@PathVariable(value = "dataSetSystemSortBy") String sortBy,
                                      @PathVariable(value = "dataSetSystemSortType") String sortType) throws IOException{
        Sort sort;
        //默认根据英文名字排序
        if(!sortBy.equals(DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME)
                && sortBy.equals(DataSetConsts.SORT_BY_DATASET_CREATE_TIME)
                && sortBy.equals(DataSetConsts.SORT_BY_DATASET_UPDATE_TIME)){
            return ResultUtil.error(-1,"排序规则不符合规定");
        }
        if(!sortBy.equals(DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME )
                || !sortType.equals(DataSetConsts.SORTTYPE_AESC)){
            logger.info("排序方式需要变更");
            sort = changSortBy(sortType,sortBy);
            // list<DataSystem>
            logger.info("按照"+sortBy+" 方式排序");
        }else {
            logger.info("默认排序");
            sort = basicSortBy();
            logger.info("按照默认方式排序");
        }
        logger.info("开始罗列所有数据集系统表：");
        List<DataSystem> dataSystemList = dataSetOptService.findAll(sort);
        return ResultUtil.success(dataSystemList);
    }

    //查询  datasetId
    @ResponseBody
    @RequestMapping(value = "selectByDataSetId/{dataSetId}",method = RequestMethod.GET)
    public ApiResult selectBydatasetId(@PathVariable(value = "dataSetId") int datasetId) throws IOException{
        DataSystem dataSystem = dataSetOptService.findByDataSetId(datasetId);
        if (dataSystem.getDatasetName().isEmpty()){
            return ResultUtil.error(-1,"所查找的数据集不存在");
        }
        return ResultUtil.success(dataSystem);
    }

    //查询  datasetName
    @ResponseBody
    @RequestMapping(value = "selectByDataSetName/{dataSetName}",method = RequestMethod.GET)
    public ApiResult selectBydatasetName(@PathVariable(value = "dataSetName") String datasetName) throws IOException{
        DataSystem dataSystem = dataSetOptService.findByDataSetName(datasetName);
        if (dataSystem.getDatasetName().isEmpty()){
            return ResultUtil.error(-1,"所查找的数据集不存在");
        }
        return ResultUtil.success(dataSystem);
    }

    //查询  userName
    @ResponseBody
    @RequestMapping(value = "selectByUser/{UserName}",method = RequestMethod.GET)
    public ApiResult selectByUserName(@PathVariable(value = "UserName") String userName) throws IOException{
        List<DataSystem> dataSystems = dataSetOptService.findByUserName(userName);
        if (dataSystems.size() != 0){
            return ResultUtil.error(-1,"所查找的数据集不存在");
        }
        return ResultUtil.success(dataSystems);
    }


    /**
     *删除
     *
     *数据集系统表删除，数据集基本表信息删除，删除数据集关于文件的信息，删除数据集hdfs 文件  删除 hive 表
     */

    @ResponseBody
    @RequestMapping(value = {"/delete/{dataSetId}"},method = RequestMethod.POST)
    public ApiResult deleteDataSet(@PathVariable(value = "dataSetId") int dataSetId)throws IOException{

        DataSystem dataSystem = dataSetOptService.findByDataSetId(dataSetId);
        logger.info("获取系统表数据集："+dataSystem);
        logger.info("准备从系统表中删除数据集：");
        int dataSetSystemId = dataSystem.getId();
        dataSetOptService.deleteById(dataSetSystemId);

        DataSet dataSet = dataSetService.findById(dataSetId);
        logger.info("获取数据集基本信息表：(对应表名)"+dataSet.getDataSetName());
        logger.info("准备从数据集基本信息表中删除数据集：");
        dataSetService.deleteById(dataSetId);

        List<DataSetFile> fileList = dataSetFileService.findDataSetFilesByDataSetId(dataSetId);
        logger.info("所属数据集的文件数量："+fileList.size());
        logger.info("准备从数据集文件表中删除数据集：");
        dataSetFileService.deleteDataSetFilesByDataSetId(dataSetId);

        try {
//            logger.info("准备删除 hive 表"+hiveService.getHiveTableName());
//            hiveService.dropHiveTable(hiveService.getHiveTableName());
//            logger.info("准备删除 hdfs中 数据集文件");
//            hdfsService.setDataSet(dataSet);
//            hdfsService.deleteFiles(hdfsService.datasetHdsfFiles());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtil.success();
    }


    private DataSystem packageDataSystem(DataSet dataSet){
        DataSystem dataSystem = new DataSystem();
        //总计11项
        dataSystem.setDatasetName(dataSet.getDataSetName());
        dataSystem.setDatasetEnglishName(dataSet.getDataSetEnglishName());
        dataSystem.setDataSetId(dataSet.getId());
        dataSystem.setUserName(dataSet.getUserName());
        dataSystem.setDatasetCreateDate(dataSet.getDataSetCreateTime());
        dataSystem.setDatasetStoreurl(dataSet.getDataSetStoreUrl());
        dataSystem.setDatasetDesc(dataSet.getDataSetBasicDesc());
        dataSystem.setDatasetHiveTablename(dataSet.getDataSetHiveTableName());
        dataSystem.setDataSetHiveTableId(dataSet.getDataSetHiveTableId());
        dataSystem.setDataSetSystemSortBy(dataSet.getDataSetSortBY());
        dataSystem.setDataSetSortType(dataSet.getDataSetSortType());
        return dataSystem;
    }

    private DataSet packageDataSet(){
        DataSet dataSet = new DataSet();
        //总计 17项
        dataSet.setUserName(DataSetConsts.DATASET_USER_NAME);
        dataSet.setUserId(DataSetConsts.DATASET_USER_ID);
        dataSet.setDataSetEnglishName(DataSetConsts.DATASET_ENGLISH_NAME);
        dataSet.setDataSetName(DataSetConsts.DATASET_CHINA_NAME);
        dataSet.setDataSetStatus(DataSetConsts.UPLOAD_STATUS_COMPLETE);
        dataSet.setDataSetUpdateDesc(null);
        dataSet.setDataSetSortBY(DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME);
        dataSet.setDataSetSortType(DataSetConsts.SORTTYPE_AESC);
        dataSet.setDataSetSize(DataSetConsts.MAX_CONTENER);
        dataSet.setDataSetFileCount(DataSetConsts.DATASET_FILECOUNT_ZERO);
        dataSet.setDataSetBasicDesc(DataSetConsts.DATASET_CHINA_NAME);
        dataSet.setDataSetPower(DataSetConsts.POWER_PRIVATE);

        long time =System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
        dataSet.setDataSetCreateTime(sd);
        dataSet.setDataSetLastUpdateTime(sd);
        dataSet.setDataSetStoreUrl(DataSetConsts.DATASET_STOREURL);
        dataSet.setDataSetHiveTableName(DataSetConsts.DATASET_ENGLISH_NAME);
        String hivetableid = DataSetConsts.DATASET_ENGLISH_NAME+"_"+time;
        dataSet.setDataSetHiveTableId(hivetableid);
        dataSet.setDatatype(DataSetConsts.DATASET_DATATYPE);
        return dataSet;
    }

    private Sort changSortBy(String orderType,String orderColumn){
        Sort sort = new Sort(Sort.Direction.fromString(orderType),orderColumn);
        return sort;
    }

    private Sort basicSortBy(){
        return changSortBy(DataSetConsts.SORTTYPE_AESC,DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME);
    }



}
