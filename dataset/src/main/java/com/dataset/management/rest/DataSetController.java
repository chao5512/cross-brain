package com.dataset.management.rest;

import com.alibaba.fastjson.JSON;
import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.consts.DataSetConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.entity.DataSystem;
import com.dataset.management.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 操作对象 : DataSet
 * 操作依据： datasetId;
 *
 * */
@Controller
@RequestMapping("dataset")
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
    HdfsService hdfsService;

    //查询  Id
    @ResponseBody
    @RequestMapping(value = "/selectById/{dataSetId}",method = RequestMethod.GET)
    public ApiResult listInfoDataSetByDataSetId(@PathVariable("dataSetId") int dataSetId){
        logger.info("开始罗列数据据基本信息");
        DataSet dataSet = dataSetService.findById(dataSetId);
        if(dataSet.getDataSetName().isEmpty()){
            return ResultUtil.error(-1,"没有找到对应的数据集名称");
        }
        return ResultUtil.success(dataSet);
    }

    //查询  datasetName
    @ResponseBody
    @RequestMapping(value = "/selectByDataSetEnglishName/{dataSetEnglishName}",method = RequestMethod.GET)
    public ApiResult listInfoDataSetByDataSetEnglishName(@PathVariable("dataSetEnglishName") String dataSetEnglishName){
        logger.info("开始罗列数据据基本信息");
        DataSet dataSet = dataSetService.findByDataSetEnglishName(dataSetEnglishName);
        if(dataSet.getId() ==0 ){
            return ResultUtil.error(-1,"没有找到对应的数据集 Id");
        }
        return ResultUtil.success(dataSet);
    }

    //查询  userName
    @ResponseBody
    @RequestMapping(value = "/selectByUserName/{UserName}",method = RequestMethod.GET)
    public ApiResult listInfoDataSetByUserName(@PathVariable("UserName") String userName){
        logger.info("开始依据用户名【 "+userName+" 】罗列数据据基本信息");
        List<DataSet> dataSets = dataSetService.findByUserName(userName);
        return ResultUtil.success(dataSets);
    }


    //查询全部
    @ResponseBody
    @RequestMapping(value = "/selectAll/{dataSetSortBy},{dataSetSortType}",method = RequestMethod.GET)
    public ApiResult selectAllDataSet(@PathVariable(value = "dataSetSortBy") String sortBy,
                                      @PathVariable(value = "dataSetSortType") String sortType) throws IOException{
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
            logger.info("数据集基本表排序方式需要变更");
            sort = changSortBy(sortType,sortBy);
            // list<DataSet>
            logger.info("按照"+sortBy+" 方式排序");
        }else {
            sort = basicSortBy();
            logger.info("按照默认方式排序");
        }

        List<DataSet> dataSets = dataSetService.findAll(sort);
        logger.info("更改所有数据集排序规则：");
        for(DataSet dataSet: dataSets){
            dataSet.setDataSetSortType(sortType);
            dataSet.setDataSetSortBY(sortBy);
            dataSetService.save(dataSet);
        }
        logger.info("开始罗列所有数据集系统表：");
        return ResultUtil.success(dataSets);
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

     * */
    @ResponseBody
    @RequestMapping(value = "/update/{updateJson}",method = RequestMethod.POST)
    public ApiResult updateDataByJson(@PathVariable("updateJson") String updateJson) throws IOException{
        DataSet dataSet = JSON.parseObject(updateJson,DataSet.class);
        int id = dataSet.getId();
        logger.info("获取修改的数据集ID："+id);
        /***
         * {"id":23,"dataSetEnglishName":"bbbbb","dataSetStatus":"COMPLETE","dataSetPower":"PRIVATE","dataSetLastUpdateTime":"rrr","dataSetUpdateDesc":null,"dataSetSortBY":null,"dataSetSortType":null,"dataSetSize":150,"dataSetBasicDesc":null,"dataSetCreateTime":null,"dataSetName":"sss","dataSetStoreUrl":null,"dataSetHiveTableName":null,"dataSetHiveTableId":null,"datatype":null,"dataSetFileCount":0}

         */
        long timetmp = System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
        dataSet.setDataSetLastUpdateTime(newTime);

        String newDesc = "update the dataset "+dataSet.getDataSetName();
        dataSet.setDataSetUpdateDesc(newDesc);

        DataSet newDataSet = dataSetService.save(dataSet);
        logger.info("更新后的数据集基本表："+newDataSet);

        int oldDatasetId = dataSet.getId();
        DataSystem old = dataSetOptService.findByDataSetId(oldDatasetId);
        int oldId = old.getId();
        logger.info("删除原有数据集系统关联表："+oldId);
        dataSetOptService.deleteById(oldId);
        DataSystem newDataSystem = packageDataSystem(dataSet);
        logger.info("重新生成关联数据集系统表：");
        dataSetOptService.save(newDataSystem);
        return ResultUtil.success(newDataSet);
    }

    /**
     * 清空
     * 删除了文件  files   更新 数据集基本表中关于文件的统计   filesCounts
     * */
    @ResponseBody
    @Transactional
    @RequestMapping(value = "/clean/{dataSetId}",method = RequestMethod.POST)
    public ApiResult cleanDataSet(@PathVariable(value = "dataSetId") int dataSetId)throws IOException{
        DataSet dataSet = dataSetService.findById(dataSetId);
        logger.info("获取数据集名称："+dataSet.getDataSetName());
        logger.info("数据集准备清空操作  （删除文件） ");
        List<DataSetFile> fileList = dataSetFileService.findDataSetFilesByDataSetId(dataSetId);
        if(fileList.size() == 0 ){
            return ResultUtil.error(-1,"此数据集不存在文件");
        }

        String hdfsTmpPath = "/tmp/user";
        int dataSetnumId = dataSet.getId();
        String tmpPath = hdfsTmpPath+"/"+dataSetnumId;

        dataSetFileService.deleteDataSetFilesByDataSetId(dataSetId);
        if(dataSetFileService.count() == 0){
            logger.info("数据集当前文件数："+dataSetFileService.count());
            logger.info("远程 hdfs 删除文件中。。");

            hdfsService.deletedir(tmpPath);
            hdfsService.mkdirHdfsDir(tmpPath);

            //需要更改3项参数
            long timetmp = System.currentTimeMillis();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
            dataSet.setDataSetLastUpdateTime(newTime);

            String newDesc = "clean the dataset: "+ dataSet.getDataSetName();
            dataSet.setDataSetUpdateDesc(newDesc);

            dataSet.setDataSetFileCount(0);
            dataSetService.save(dataSet);
            logger.info("更新后的数据及基本表新信息："+dataSet);
            return ResultUtil.success();
        }
        return ResultUtil.error(-1,"清空失败");
    }

    private Sort changSortBy(String orderType,String orderColumn){
        Sort sort = new Sort(Sort.Direction.fromString(orderType),orderColumn);
        return sort;
    }

    private Sort basicSortBy(){
        return new Sort(Sort.Direction.fromString(DataSetConsts.SORTTYPE_ASC),DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME);
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


}
