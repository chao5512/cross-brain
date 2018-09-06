package com.dataset.management.controller;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.config.HdfsConfig;
import com.dataset.management.constant.DataSetConstants;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    //dataset元数据服务
    @Autowired
    DataSetMetastoreService metastoreService;

    //hive操作相关服务
    @Autowired
    HiveTableService tableService;

    //创建
    @ResponseBody
    @ApiOperation(value = "创建数据集",httpMethod = "POST")
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public ApiResult createDataSet(@RequestParam(value = "dataSetName") String dataSetName,
                                   @RequestParam(value = "dataSetEnglishName",required = false,defaultValue = "") String dataSetEnglishName,
                                   @RequestParam(value = "dataSetDesc") String datSetDesc,
                                   @RequestParam(value = "dataSetPower") int dataSetPower,
                                   @RequestParam(value = "userId") int userId) throws IOException{
        //先生成默认的
        DataSet dataSet = packageDataSet();
        dataSet.setDataSetName(dataSetName);
        dataSet.setUserId(userId);
        dataSet.setDataSetBasicDesc(datSetDesc);
        dataSet.setDataSetPower(dataSetPower);
        dataSet.setDataSetEnglishName(dataSetEnglishName);

        //对用户隐藏
        String hdfsHOstName = hdfsConfig.getHostName();
        Long hdfsPorthide = hdfsConfig.getHdfsProt();
        String dataStoreUrlhide = hdfsHOstName+":"+hdfsPorthide+DataSetConstants.DATASET_STOREURL_DIR
                +"/"+userId+"/"+dataSetName;
        dataSet.setDataSetStoreUrl(dataStoreUrlhide);

        //用于外部链接
        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();
        String dataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConstants.DATASET_STOREURL_DIR
                +"/"+userId+"/"+dataSetName;

        Sort sort = basicSortBy();
        try {
            List<DataSet> dataSets = dataSetService.findAll(sort);
            List<String> cnDatasetNames = listName(dataSets);
            if(cnDatasetNames.contains(dataSet.getDataSetName())){
                return ResultUtil.error(2001,"数据集名称已经存在,请重新命名。。。");
            }
            logger.info("检测远程hdfs 相关目录(数据集和模型)");
            if(!hdfsService.existDir(dataStoreUrl,false)){
                hdfsService.mkdirHdfsDir(dataStoreUrl);
            }else {
                return ResultUtil.error(2001,"数据集文件夹已经存在，请在 hdfs 中删除后重新创建");
            }
            logger.info("准备创建的数据集: "+ dataSetName);

            //数据集表信息
            logger.info("数据集基本表创建。。。。。。");
            DataSet dataSetcreate = dataSetService.save(dataSet);

            return  ResultUtil.success(dataSetService.save(dataSetcreate));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("创建失败");
            return ResultUtil.error(2001,"创建失败");
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
                return ResultUtil.error(2002,"没有找到对应的数据集名称");
            }
            return ResultUtil.success(dataSet);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败");
            return ResultUtil.error(2002,"查询失败");
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
                return ResultUtil.error(2003,"没有找到对应的数据集 Id");
            }
            return ResultUtil.success(dataSet);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败");
            return ResultUtil.error(2003,"查询失败");
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
                return ResultUtil.error(2004,"没有找到对应的数据集");
            }
            return ResultUtil.success(dataSet);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("查询失败");
            return ResultUtil.error(2004,"查询失败");
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
                return ResultUtil.error(2005,"未找到对应数据集");
            }
            return ResultUtil.success(dataSets);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败");
            return ResultUtil.error(2005,"查询失败");
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
                return ResultUtil.error(2006,"所查找的数据集不存在");
            }
            return ResultUtil.success(dataSets);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败");
            return ResultUtil.error(2006,"查询失败");
        }
    }

    /**
     String dataSetStatus          状态随动，依据数据集文件上传的状态而定
     String dataSetLastUpdateTime  状态随动  伴随更改操作而定；
     int filesCount：              状态随动  依据数据集内文件数量变更；
     * */
    @ApiOperation(value = "依据客户端数据集属性，修改数据集",httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public ApiResult updateDataByJson(@RequestParam(value = "newDataSetName") String newDataSetName,
                                      @RequestParam(value = "newDataSetDesc") String newDataSetDesc,
                                      @RequestParam(value = "newDataSetPower") int newDataSetPower,
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
            int userId =dataSet.getUserId();
            String oldDataSetName = dataSet.getDataSetName();

            //外部链接地址
            String hdfsUrl = hdfsConfig.getHdfsUrl();
            Long hdfsPortold = hdfsConfig.getHdfsProt();
            String oldDataStoreUrl = hdfsUrl+":"+hdfsPortold + DataSetConstants.DATASET_STOREURL_DIR
                    +"/"+userId+"/"+oldDataSetName;

            //新的用户隐藏字段（地址）
            String hdfsHOstName = hdfsConfig.getHostName();
            Long hdfsPort = hdfsConfig.getHdfsProt();
            String newdataStoreUrlhide = hdfsHOstName+":"+hdfsPort+DataSetConstants.DATASET_STOREURL_DIR
                    +"/"+userId+"/"+newDataSetName;
            dataSet.setDataSetStoreUrl(newdataStoreUrlhide);//new

            //外部链接地址
            String newdataStoreUrl = hdfsUrl+":"+hdfsPortold + DataSetConstants.DATASET_STOREURL_DIR
                    +"/"+userId+"/"+oldDataSetName;

            if(hdfsService.existDir(oldDataStoreUrl,false)){
                hdfsService.renameDir(oldDataStoreUrl,newdataStoreUrl);
            }else {
                return ResultUtil.error(2007,"原旧数据集文件夹不存在，请在 hdfs 中确认后重新修改");
            }

            dataSet.setDataSetBasicDesc(newDataSetDesc);    //desc
            dataSet.setDataSetName(newDataSetName);         //name
            dataSet.setDataSetPower(newDataSetPower);       //power

            DataSet newDataSet = dataSetService.save(dataSet);
            logger.info("更新后的数据集基本表："+newDataSet);
            return ResultUtil.success(newDataSet);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logger.error("修改失败");
            return ResultUtil.error(2007,"修改失败");
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
                return ResultUtil.error(2008,"此数据集不存在文件");
            }

            String dataSetName =dataSet.getDataSetName();
            int userId = dataSet.getUserId();

            String tmpPath = DataSetConstants.DATASET_STOREURL_DIR
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

                dataSet.setDataSetFileCount(0);
                dataSetService.save(dataSet);
                logger.info("更新后的数据及基本表新信息："+dataSet);
                return ResultUtil.success();
            }
            return ResultUtil.error(2008,"清空失败");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logger.error("清空失败");
            return ResultUtil.error(2008,"清空失败");
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
            logger.error("删除失败");
            return ResultUtil.error(2009,"删除失败");
        }

        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();
        String dataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConstants.DATASET_STOREURL_DIR
                +"/"+userId+"/"+dataSetName;
        String tmpUrl = DataSetConstants.DATASET_STOREURL_DIR
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
            logger.error("删除失败");
            return ResultUtil.error(2009,"删除失败");
        }
        return ResultUtil.success();
    }


    //相关方法
    private Sort changSortBy(String orderType,String orderColumn){
        Sort sort = new Sort(Sort.Direction.fromString(orderType),orderColumn);
        return sort;
    }
    private Sort basicSortBy(){
        List<Sort.Order> sortList = new ArrayList<Sort.Order>();
        Sort.Order datasetNameSortBy = new Sort.Order(Sort.Direction.DESC,DataSetConstants.SORT_BY_DATASET_NAME);
        Sort.Order datasetPowerSortBy = new Sort.Order(Sort.Direction.DESC,DataSetConstants.SORT_BY_DATASET_POWER);
        Sort.Order datasetUploadSortBy = new Sort.Order(Sort.Direction.DESC,DataSetConstants.SORT_BY_DATASET_CREATE_TIME);
        sortList.add(datasetPowerSortBy);
        sortList.add(datasetUploadSortBy);
        sortList.add(datasetNameSortBy);
        return new Sort(sortList);
    }
    private DataSet packageDataSet(){
        DataSet dataSet = new DataSet();
        //总计 17项
        dataSet.setUserId(DataSetConstants.DATASET_USER_ID);
        dataSet.setDataSetEnglishName(DataSetConstants.DATASET_ENGLISH_NAME);
        dataSet.setDataSetName(DataSetConstants.DATASET_CHINA_NAME);
        dataSet.setDataSetStatus(DataSetConstants.UPLOAD_STATUS_COMPLETE);

        dataSet.setDataSetSize(DataSetConstants.MAX_CONTENER);
        dataSet.setDataSetFileCount(DataSetConstants.DATASET_FILECOUNT_ZERO);
        dataSet.setDataSetBasicDesc(DataSetConstants.DATASET_BASIC_DESC);
        dataSet.setDataSetPower(DataSetConstants.POWER_PRIVATE);

        long time =System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
        dataSet.setDataSetCreateTime(sd);
        dataSet.setDataSetLastUpdateTime(sd);
        dataSet.setDataSetStoreUrl(null);
        dataSet.setDataSetHiveTableName(DataSetConstants.DATASET_ENGLISH_NAME);

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

    /**
     * 功能描述:根据用户id查数据集名字
     * @param userId
     * @return: com.dataset.management.common.ApiResult
     * @auther: 王培文
     * @date: 2018/8/27 17:18
     */
    @ResponseBody
    @ApiOperation(value = "根据用户id查数据集名字",httpMethod = "POST")
    @RequestMapping(value = "dataSetTableName",method = RequestMethod.POST)
    public ApiResult getDataSetName(@RequestParam("userId") String userId){
        try {
            logger.debug("用户id" + userId);
            List<DataSet> dataSets = dataSetService.findByUserId(Integer.parseInt(userId));
            List<String> dataSetTableNameList = new ArrayList<>();
            for (DataSet dataSet:dataSets) {
                String tableName = tableService.getTableNameByDataSet(dataSet);
                if(tableName != null){
                    dataSetTableNameList.add(tableName);
                }
            }
            return ResultUtil.success(dataSetTableNameList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.valueOf(e.getStackTrace()));
            return ResultUtil.error(2016,"获取表明失败");
        }
    }

    /**
     * 功能描述: 根据表名获取所有列信息
     * @param tableName
     * @return: com.dataset.management.common.ApiResult
     * @auther: 王培文
     * @date: 2018/8/27 17:24
     */
    @ResponseBody
    @ApiOperation(value = "根据表名获取所有列信息",httpMethod = "POST")
    @RequestMapping(value="dataSetColumns",method = RequestMethod.POST)
    public ApiResult getDataSetColumns(@RequestParam("tableName") String tableName){
        try {
            logger.debug("表名：" + tableName);
            HiveTableMeta tableMeta = metastoreService.getHiveTableMeta(tableName);
            List<FieldMeta> fields = tableMeta.getFields();
            //fieldNameList 存放查询出来的表字段信息
            List<String> fieldNameList = new ArrayList<String>();
            for (FieldMeta field:fields) {
                String fieldName = field.getFieldName();
                fieldNameList.add(fieldName);
            }
            return  ResultUtil.success(fieldNameList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.valueOf(e.getStackTrace()));
            return ResultUtil.error(2015,"获取表字段信息失败");
        }
    }


}
