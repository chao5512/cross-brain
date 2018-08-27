package com.dataset.management.controller;

import com.alibaba.fastjson.JSON;
import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.config.HdfsConfig;
import com.dataset.management.consts.DataSetConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 操作对象 : DataSet
 * 操作依据： datasetId;
 *
 * */
@Api(value = "数据集详情表",description = "数据集详情表API")
@Controller
@RequestMapping("dataset")
public class DataSetController {

    private static Logger logger = LoggerFactory.getLogger(DataSetController.class);

    @Autowired
    IntDataSetService dataSetService;

    @Autowired
    IntDataSetFileService dataSetFileService;

    @Autowired
    HdfsService hdfsService;

    @Autowired
    HdfsConfig hdfsConfig;

    //创建
    @ResponseBody
    @ApiOperation(value = "创建数据集",httpMethod = "POST")
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public ApiResult createDataSet(@RequestParam(value = "dataSetName") String dataSetName,
                                   @RequestParam(value = "dataSetEnglishName") String dataSetEnglishName,
                                   @RequestParam(value = "dataSetDesc") String datSetDesc,
                                   @RequestParam(value = "dataSetPower") String dataSetPower,
                                   @RequestParam(value = "userId") int userId,
                                   @RequestParam(value = "dataSetFileType") String dataSetFileType,
                                   @RequestParam(value = "userName") String userName) throws IOException{
        //先生成默认的
        DataSet dataSet = packageDataSet();
        dataSet.setDataSetName(dataSetName);
        dataSet.setUserId(userId);
        dataSet.setUserName(userName);
        dataSet.setDataSetBasicDesc(datSetDesc);
        dataSet.setDataSetPower(dataSetPower);
        dataSet.setDataSetEnglishName(dataSetEnglishName);
        dataSet.setDatatype(dataSetFileType);

        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();
        String dataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConsts.DATASET_STOREURL_DIR
                +"/"+userId+"/"+dataSetName;
        dataSet.setDataSetStoreUrl(dataStoreUrl);

        Sort sort = basicSortBy();
        try {
            List<DataSet> dataSets = dataSetService.findAll(sort);
            List<String> cnDatasetNames = listName(dataSets);
            if(cnDatasetNames.contains(dataSet.getDataSetName())){
                return ResultUtil.error(-1,"数据集名称已经存在,请重新命名。。。");
            }
            logger.info("检测远程hdfs 相关目录(数据集和模型)");
            if(!hdfsService.existDir(dataStoreUrl,false)){
                hdfsService.mkdirHdfsDir(dataStoreUrl);
            }else {
                return ResultUtil.error(-1,"数据集文件夹已经存在，请在 hdfs 中删除后重新创建");
            }
            logger.info("准备创建的数据集: "+ dataSetName);

            //数据集表信息
            logger.info("数据集基本表创建。。。。。。");
            DataSet dataSetcreate = dataSetService.save(dataSet);
            int datasetId = dataSetcreate.getId();

            return  ResultUtil.success(dataSetService.save(dataSetcreate));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"创建失败");
        }
    }

    //查询  Id
    @ApiOperation(value = "依据指定的数据集ID，查询数据集详情",httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "/selectById",method = RequestMethod.GET)
    public ApiResult listInfoDataSetByDataSetId(@RequestParam("dataSetId") int dataSetId){
        logger.info("开始罗列数据据基本信息");
        try {
            DataSet dataSet = dataSetService.findById(dataSetId);
            if(dataSet.getDataSetName().isEmpty()){
                return ResultUtil.error(-1,"没有找到对应的数据集名称");
            }
            return ResultUtil.success(dataSet);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"查询失败");
        }
    }

    //查询  datasetEnglishName
    @ApiOperation(value = "依据指定的数据集英文名称名称，查询数据集详情",httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "/selectByDataSetEnglishName",method = RequestMethod.GET)
    public ApiResult listInfoDataSetByDataSetEnglishName(@RequestParam("dataSetEnglishName") String dataSetEnglishName,
                                                         @RequestParam(value = "userId") int userId){
        logger.info("开始罗列数据据基本信息");
        try {
            DataSet dataSet = dataSetService.findByDataSetEnglishNameAndUserId(dataSetEnglishName,userId);
            if(dataSet.getId() ==0 ){
                return ResultUtil.error(-1,"没有找到对应的数据集 Id");
            }
            return ResultUtil.success(dataSet);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"查询失败");
        }
    }

    //查询  datasetName
    @ApiOperation(value = "依据指定的数据集名称，查询数据集详情",httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "/selectByDataSetName",method = RequestMethod.GET)
    public ApiResult listInfoDataSetByDataSetName(@RequestParam("dataSetName") String dataSetName,
                                                  @RequestParam("userId")int userId) throws IOException{
        logger.info("开始罗列数据据基本信息");
        try {
            DataSet dataSet = dataSetService.findByDataSetNameAndUserId(dataSetName,userId);
            if(dataSet.getDataSetName().isEmpty()){
                return ResultUtil.error(-1,"没有找到对应的数据集");
            }
            return ResultUtil.success(dataSet);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"查询失败");
        }
    }

    //模糊查询  datasetNameLike
    @ApiOperation(value = "依据指定的数据集名称，查询数据集详情",httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "/selectByDataSetNameLike",method = RequestMethod.GET)
    public ApiResult listInfoDataSetByDataSetNameLike(@RequestParam("dataSetName") String dataSetNameLike,
                                                  @RequestParam("userId")int userId) throws IOException{
        logger.info("开始罗列数据据基本信息");
        try {
            List<DataSet> dataSets = dataSetService.findByUserIdAndDataSetNameLike(userId,dataSetNameLike);
            if(dataSets.size()==0){
                return ResultUtil.error(-1,"未找到对应数据集");
            }
            return ResultUtil.success(dataSets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"查询失败");
        }
    }

    //查询全部  user  Id
    @ApiOperation(value = "依据指定的用户ID，查询所有数据集详情",httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "/selectByUserId",method = RequestMethod.GET)
    public ApiResult listInfoDataSetByUserId(@RequestParam("UserId") int userId){
        logger.info("开始依据用户Id【 "+userId+" 】罗列数据据基本信息");
        try {
            List<DataSet> dataSets = dataSetService.findByUserId(userId);
            if (dataSets.size()==0){
                return ResultUtil.error(-1,"所查找的数据集不存在");
            }
            return ResultUtil.success(dataSets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"查询失败");
        }
    }
//
//
//    //查询全部
//    @ApiOperation(value = "变更数据集排序方式",httpMethod = "GET")
//    @ResponseBody
//    @RequestMapping(value = "/selectAll",method = RequestMethod.GET)
//    public ApiResult selectAllDataSet(@RequestParam(value = "dataSetSortBy") String sortBy,
//                                      @RequestParam(value = "dataSetSortType") String sortType) throws IOException{
//        Sort sort;
//        //默认根据英文名字排序
//        if(!sortBy.equals(DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME)
//                && !sortBy.equals(DataSetConsts.SORT_BY_DATASET_CREATE_TIME)
//                && !sortBy.equals(DataSetConsts.SORT_BY_DATASET_UPDATE_TIME)
//                && !sortBy.equals(DataSetConsts.SORT_BY_DATASET_POWER)){
//            return ResultUtil.error(-1,"排序规则不符合规定");
//        }
//        if(!sortBy.equals(DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME )
//                || !sortType.equals(DataSetConsts.SORTTYPE_ASC)){
//            logger.info("数据集基本表排序方式需要变更");
//            sort = changSortBy(sortType,sortBy);
//            // list<DataSet>
//            logger.info("按照"+sortBy+" 方式排序");
//        }else {
//            sort = basicSortBy();
//            logger.info("按照默认方式排序");
//        }
//
//        List<DataSet> dataSets = dataSetService.findAll(sort);
//        logger.info("更改所有数据集排序规则：");
//        for(DataSet dataSet: dataSets){
//            dataSet.setDataSetSortType(sortType);
//            dataSet.setDataSetSortBY(sortBy);
//            dataSetService.save(dataSet);
//        }
//        logger.info("开始罗列所有数据集系统表：");
//        return ResultUtil.success(dataSets);
//    }


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
    @ApiOperation(value = "依据客户端数据集属性，修改数据集",httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public ApiResult updateDataByJson(@RequestParam(value = "newDataSetName") String newDataSetName,
                                      @RequestParam(value = "newDataSetDesc") String newDataSetDesc,
                                      @RequestParam(value = "newDataSetPower") String newDataSetPower,
                                      @RequestParam(value = "dataSetId") int dataSetId) throws IOException{

        try {
            DataSet dataSet = dataSetService.findById(dataSetId);
            int id = dataSet.getId();
            logger.info("获取修改的数据集ID："+id);
            /***
             * {"id":23,"dataSetEnglishName":"bbbbb","dataSetStatus":"COMPLETE","dataSetPower":"PRIVATE","dataSetLastUpdateTime":"rrr","dataSetUpdateDesc":null,"dataSetSortBY":null,"dataSetSortType":null,"dataSetSize":150,"dataSetBasicDesc":null,"dataSetCreateTime":null,"dataSetName":"sss","dataSetStoreUrl":null,"dataSetHiveTableName":null,"dataSetHiveTableId":null,"datatype":null,"dataSetFileCount":0}

             */
            long timetmp = System.currentTimeMillis();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
            dataSet.setDataSetLastUpdateTime(newTime);

            String oldDataStoreUrl = dataSet.getDataSetStoreUrl(); //old
            String hdfsUrl = hdfsConfig.getHdfsUrl();
            Long hdfsPort = hdfsConfig.getHdfsProt();
            int userId = dataSet.getUserId();
            String newdataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConsts.DATASET_STOREURL_DIR
                    +"/"+userId+"/"+newDataSetName;
            dataSet.setDataSetStoreUrl(newdataStoreUrl);//new

            if(!hdfsService.existDir(oldDataStoreUrl,false)){
                hdfsService.renameDir(oldDataStoreUrl,newdataStoreUrl);
            }else {
                return ResultUtil.error(-1,"原旧数据集文件夹不存在，请在 hdfs 中确认后重新修改");
            }

            String newDesc = "update the dataset "+dataSet.getDataSetName();
            dataSet.setDataSetUpdateDesc(newDesc);

            dataSet.setDataSetBasicDesc(newDataSetDesc);    //desc
            dataSet.setDataSetName(newDataSetName);         //name
            dataSet.setDataSetPower(newDataSetPower);       //power

            DataSet newDataSet = dataSetService.save(dataSet);
            logger.info("更新后的数据集基本表："+newDataSet);
            return ResultUtil.success(newDataSet);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"修改失败");
        }
    }

    /**
     * 清空
     * 删除了文件  files   更新 数据集基本表中关于文件的统计   filesCounts
     * */
    @ApiOperation(value = "依据指定的数据集ID，清空数据集",httpMethod = "POST")
    @ResponseBody
    @Transactional
    @RequestMapping(value = "/clean",method = RequestMethod.POST)
    public ApiResult cleanDataSet(@RequestParam(value = "dataSetId") int dataSetId)throws IOException{
        try {
            DataSet dataSet = dataSetService.findById(dataSetId);
            logger.info("获取数据集名称："+dataSet.getDataSetName());
            logger.info("数据集准备清空操作  （删除文件） ");
            List<DataSetFile> fileList = dataSetFileService.findDataSetFilesByDataSetId(dataSetId);
            if(fileList.size() == 0 ){
                return ResultUtil.error(-1,"此数据集不存在文件");
            }

            String dataSetName =dataSet.getDataSetName();
            int userId = dataSet.getUserId();

            String tmpPath = DataSetConsts.DATASET_STOREURL_DIR
                    +"/"+userId+"/"+dataSetName;

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

                String newDesc = "clean the dataset: "+ dataSetName;
                dataSet.setDataSetUpdateDesc(newDesc);

                dataSet.setDataSetFileCount(0);
                dataSetService.save(dataSet);
                logger.info("更新后的数据及基本表新信息："+dataSet);
                return ResultUtil.success();
            }
            return ResultUtil.error(-1,"清空失败");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"清空失败");
        }
    }

    //删除
    @ApiOperation(value = "依据指定的数据集ID，删除数据集",httpMethod = "POST")
    @ResponseBody
    @javax.transaction.Transactional
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public ApiResult deleteDataSet(@RequestParam(value = "dataSetId") int dataSetId,
                                   @RequestParam(value = "userId") int userId)throws IOException{

        String dataSetName = null;
        try {
            DataSet dataSet = dataSetService.findById(dataSetId);
            dataSetName = dataSet.getDataSetName();
            logger.info("获取数据集："+dataSetName);
            logger.info("准备从系统表中删除数据集：");
            dataSetService.deleteById(dataSetId);

            List<DataSetFile> fileList = dataSetFileService.findDataSetFilesByDataSetId(dataSetId);
            logger.info("所属数据集的文件数量："+fileList.size());
            if(fileList.size() ==0 ){
                logger.info("数据集是空的。不用执行删除文件操作");
            }else {
                logger.info("准备从数据集文件表中删除数据集：");
                dataSetFileService.deleteDataSetFilesByDataSetId(dataSetId);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"删除失败");
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


    //相关方法
    private Sort changSortBy(String orderType,String orderColumn){
        Sort sort = new Sort(Sort.Direction.fromString(orderType),orderColumn);
        return sort;
    }
    private Sort basicSortBy(){
        return new Sort(Sort.Direction.fromString(DataSetConsts.SORTTYPE_ASC),DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME);
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
        dataSet.setDataSetSortBY(DataSetConsts.SORT_BY_DATASET_NAME);
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
        dataSet.setDataSetHiveTableId(null);
        dataSet.setDatatype(DataSetConsts.DATASET_DATATYPE);
        return dataSet;
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
