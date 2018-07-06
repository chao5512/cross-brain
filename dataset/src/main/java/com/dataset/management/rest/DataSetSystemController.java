package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.config.HdfsConfig;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

@Controller
@RequestMapping("datasetSystem")
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
    HiveTableService hiveTableService;

    @Autowired
    HdfsService hdfsService;

    @Autowired
    HdfsConfig hdfsConfig;

    private static final ExecutorService exeService = Executors.newFixedThreadPool(5);

    @ResponseBody
    @RequestMapping(value = "/create/{dataSetName}/{userId}/{dataSetDesc}",method = RequestMethod.POST)
    public ApiResult createDataSet(@PathVariable(value = "dataSetName") String dataSetName,
                                   @PathVariable(value = "dataSetDesc") String datSetDesc,
                                   @PathVariable(value = "userId") int userId) throws IOException{
        //先生成默认的
        DataSet dataSet = packageDataSet();
        dataSet.setDataSetName(dataSetName);
        dataSet.setUserId(userId);
        dataSet.setDataSetBasicDesc(datSetDesc);
        dataSet.setDataSetHiveTableName(null);
        dataSet.setDataSetHiveTableId(null);
        dataSet.setDataSetEnglishName(null);
        dataSet.setDataSetStoreUrl(null);

        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();
        String dataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConsts.DATASET_STOREURL_DIR
                +"/"+userId+"/"+dataSetName;
        dataSet.setDataSetStoreUrl(dataStoreUrl);

        Sort sort = basicSortBy();
        List<DataSet> dataSets = dataSetService.findAll(sort);
        List<String> cnDatasetNames = listName(dataSets);
        if(cnDatasetNames.contains(dataSet.getDataSetName())){
            return ResultUtil.error(-1,"数据集名称已经存在,请重新命名。。。");
        }
        logger.info("检测远程hdfs 相关目录");
        if(!hdfsService.existDir(dataStoreUrl,false)){
            hdfsService.mkdirHdfsDir(dataStoreUrl);
        }else {
            return ResultUtil.error(-1,"数据集文件夹已经存在，请在 hdfs 中删除后重新创建");
        }
        logger.info("准备创建的数据集: "+ JSON.toString(dataSet));

        //数据集表信息
        logger.info("数据集基本表创建。。。。。。");
        DataSet dataSetcreate = dataSetService.save(dataSet);
        int datasetId = dataSetcreate.getId();

        DataSystem newDataSystem = packageDataSystem(dataSetcreate);
        logger.info("当前数据集Id"+datasetId);
        newDataSystem.setDataSetId(datasetId);
        //体统表信息
        logger.info("数据集系统表开始创建：。。。。。");
        return  ResultUtil.success(dataSetOptService.save(newDataSystem));
    }
    //查询
    @ResponseBody
    @RequestMapping(value = "/selectAll/{dataSetSystemSortBy},{dataSetSystemSortType}",method = RequestMethod.GET)
    public ApiResult selectAllDataSet(@PathVariable(value = "dataSetSystemSortBy") String sortBy,
                                      @PathVariable(value = "dataSetSystemSortType") String sortType) throws IOException{
        Sort sort;
        //默认根据英文名字排序
        if(!sortBy.equals(DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME)
                && !sortBy.equals(DataSetConsts.SORT_BY_DATASET_CREATE_TIME)
                && !sortBy.equals(DataSetConsts.SORT_BY_DATASET_UPDATE_TIME)
                && !sortBy.equals(DataSetConsts.SORT_BY_DATASET_POWER)){
            return ResultUtil.error(-1,"排序规则不符合规定");
        }
        if(!sortBy.equals(DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME )
                || !sortType.equals(DataSetConsts.SORTTYPE_ASC)){
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
    @RequestMapping(value = "/selectByDataSetId/{dataSetId}",method = RequestMethod.GET)
    public ApiResult selectBydatasetId(@PathVariable(value = "dataSetId") int datasetId) throws IOException{
        DataSystem dataSystem = dataSetOptService.findByDataSetId(datasetId);
        if (dataSystem.getDatasetName().isEmpty()){
            return ResultUtil.error(-1,"所查找的数据集不存在");
        }
        return ResultUtil.success(dataSystem);
    }

    //查询  datasetName
    @ResponseBody
    @Transactional
    @RequestMapping(value = "/selectByDataSetName/{dataSetName}",method = RequestMethod.GET)
    public ApiResult selectBydatasetName(@PathVariable(value = "dataSetName") String datasetName) throws IOException{
        DataSystem dataSystem = dataSetOptService.findByDataSetName(datasetName);
        logger.info("......."+dataSystem);
        logger.info("查询当前数据集名称："+dataSystem.getDatasetName());
        if (dataSystem.getDatasetName().isEmpty()){
            return ResultUtil.error(-1,"所查找的数据集不存在");
        }
        return ResultUtil.success(dataSystem);
    }

    //查询  userId
    @ResponseBody
    @Transactional
    @RequestMapping(value = "/selectByUserId/{UserId}",method = RequestMethod.GET)
    public ApiResult selectByUserId(@PathVariable(value = "UserId") int userId) throws IOException{
        List<DataSystem> dataSystems = dataSetOptService.findByUserId(userId);
        logger.info("获取用户Id："+dataSystems.get(0).getUserId());
        if (dataSystems.get(0).getUserId()==0){
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
    @Transactional
    @RequestMapping(value = "/delete/{dataSetId}",method = RequestMethod.POST)
    public ApiResult deleteDataSet(@PathVariable(value = "dataSetId") int dataSetId)throws IOException{

        DataSystem dataSystem = dataSetOptService.findByDataSetId(dataSetId);
        logger.info("获取系统表数据集："+dataSystem);
        logger.info("准备从系统表中删除数据集：");
        int dataSetSystemId = dataSystem.getId();
        dataSetOptService.deleteById(dataSetSystemId);

        DataSet dataSet = dataSetService.findById(dataSetId);
        int userId =dataSet.getUserId();
        String dataSetName = dataSet.getDataSetName();
        logger.info("获取数据集基本信息表：(对应表名)"+dataSet.getDataSetName());
        logger.info("准备从数据集基本信息表中删除数据集：");
        dataSetService.deleteById(dataSetId);

        List<DataSetFile> fileList = dataSetFileService.findDataSetFilesByDataSetId(dataSetId);
        logger.info("所属数据集的文件数量："+fileList.size());
        if(fileList.size() ==0 ){
            logger.info("数据集是空的。不用执行删除文件操作");
        }else {
            logger.info("准备从数据集文件表中删除数据集：");
            dataSetFileService.deleteDataSetFilesByDataSetId(dataSetId);
        }

        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();
        String dataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConsts.DATASET_STOREURL_DIR
                +"/"+userId+"/"+dataSetName;
        String tmpUrl = DataSetConsts.DATASET_STOREURL_DIR
                +"/"+userId+"/"+dataSetName;


        try {
            logger.info("hdfs 库中开始删除相关数据集：");
            logger.info("hdfs:" +dataStoreUrl);
            if(hdfsService.existDir(tmpUrl,false)){
                logger.info(hdfsService.existDir(tmpUrl,false)+"  判断目录是否存在");
                logger.info("已找到 hdfs 对应文件夹");
                hdfsService.deletedir(tmpUrl);
            }
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
        dataSystem.setUserName(dataSet.getUserName());
        dataSystem.setUserId(dataSet.getUserId());
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
        dataSet.setDataSetSortType(DataSetConsts.SORTTYPE_ASC);
        dataSet.setDataSetSize(DataSetConsts.MAX_CONTENER);
        dataSet.setDataSetFileCount(DataSetConsts.DATASET_FILECOUNT_ZERO);
        dataSet.setDataSetBasicDesc(DataSetConsts.DATASET_BASIC_DESC);
        dataSet.setDataSetPower(DataSetConsts.POWER_PRIVATE);

        long time =System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
        dataSet.setDataSetCreateTime(sd);
        dataSet.setDataSetLastUpdateTime(sd);
        dataSet.setDataSetStoreUrl(null);
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
//        return changSortBy(DataSetConsts.SORTTYPE_ASC,DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME);
        return new Sort(Sort.Direction.fromString("asc"),"dataSetCreateTime");
    }

    public  List<String> listName(List<DataSet> dataSets){
        List<String> ss = new ArrayList<>();
        for(DataSet dataSet: dataSets){
            String name = dataSet.getDataSetName();
            if(!ss.contains(name)){
                ss.add(name);
            }
        }
        return ss;
    }
    public  List<String> listEnglishName(List<DataSet> dataSets){
        List<String> ss = new ArrayList<>();
        for(DataSet dataSet: dataSets){
            String name = dataSet.getDataSetEnglishName();
            if(!ss.contains(name)){
                ss.add(name);
            }
        }
        return ss;
    }



}
