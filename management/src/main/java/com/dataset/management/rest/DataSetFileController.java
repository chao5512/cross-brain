package com.dataset.management.rest;

import com.alibaba.fastjson.JSON;
import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.consts.DataSetConsts;
import com.dataset.management.consts.DataSetFileConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.service.IntDataSetFileService;
import com.dataset.management.service.IntDataSetOptService;
import com.dataset.management.service.IntDataSetService;
import org.apache.spark.ml.source.libsvm.LibSVMDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("datasetFile")
@CrossOrigin
public class DataSetFileController {
    @Autowired
    IntDataSetService dataSetService;

    @Autowired
    IntDataSetFileService dataSetFileService;

    @Autowired
    IntDataSetOptService dataSetOptService;

//    @Autowired
//    HdfsService hdfsService;


    private static Logger logger = LoggerFactory.getLogger(DataSetFileController.class);

    //上传
    @ResponseBody
    @RequestMapping(value = "/upload/{files},{filesSortBy},{fileSortType},{dataSetId}",method = RequestMethod.POST)
    public ApiResult uploadFilestoDataSet(@PathVariable(value = "files") List<String> filesName,
                                          @PathVariable(value = "filesSortBy") String  sortBy,
                                          @PathVariable(value = "fileSortType") String  sortType,
                                          @PathVariable(value = "dataSetId") int dataSetId) throws IOException{

        Sort sort;
        DataSet dataSet = dataSetService.findById(dataSetId);
        logger.info("修改当前数据集上传文件状态：");
        dataSet.setDataSetStatus(DataSetConsts.UPLOAD_STATUS_LOADING);
        dataSetService.save(dataSet);
        List<DataSetFile> dataSetFiles = packageDataSetFiles(filesName,dataSet);

        logger.info("获取当前数据集中已经存在的文件名称列表：");
        List<DataSetFile> estsDataSetFiles = dataSetFileService.findDataSetFilesByDataSetId(dataSetId);
        List<String> estsFilesNmaes = dataSetFileService.isExistsFiles(estsDataSetFiles);
        logger.info("当前数据集存在文件列表："+estsDataSetFiles);
        for(DataSetFile setFile: dataSetFiles){
            String name = setFile.getFileName();
            if(!estsFilesNmaes.contains(name)){
                dataSetFileService.save(setFile);
            }
            logger.info("改文件已经在数据库中，请重新命名后重新上传: "+setFile.getFileName());
        }
        logger.info(" 准备 上传至hdfs");
//        String hdfsurl = hdfsService.conterHdfsUrl();
//        logger.info("hdfs 路径："+ hdfsurl);
//        hdfsService.uploadFiles(filesName,hdfsurl);
        logger.info("上传完毕，数据集状态更改");
        dataSet.setDataSetStatus(DataSetConsts.UPLOAD_STATUS_COMPLETE);
        logger.info("修改时对应数据集上文件数：");
        dataSet.setDataSetFileCount(dataSetFiles.size());
        dataSetService.save(dataSet);

        if( sortBy != null && sortType != null){
             sort = changSortBy(sortType,sortBy);
        }
        logger.info("数据集文件排序方式："+sortBy+"  "+sortType);
        sort = basicSortBy();
        logger.info("查看上传后数据集文件的列表：");
        List<DataSetFile> newFileList = dataSetFileService.findAll(sort);
        return ResultUtil.success(newFileList);
    }

    //查询
    @ResponseBody
    @RequestMapping(value = {"/listFileAll/{filesSortBy},{filesSortType},{dataSetId}"},method = RequestMethod.GET)
    public ApiResult selectAllFiles(@PathVariable(value = "filesSortBy") String sortBy,
                                    @PathVariable(value = "filesSortType") String sortType,
                                    @PathVariable(value = "dataSetId") int dataSetId){
        int code = 0;
        String message =" 查询数据集文件信息：";
        Sort sort;
        if( sortBy != null && sortType != null){
            sort = changSortBy(sortType,sortBy);
        }
        logger.info("数据集文件排序方式："+sortBy+"  "+sortType);
        sort = basicSortBy();
        logger.info("查看上传后数据集文件的列表：");
        List<DataSetFile> fileList = dataSetFileService.findDataSetFilesByDataSetId(dataSetId,sort);
        return ResultUtil.success(fileList);
    }

    //查询  id
    @ResponseBody
    @RequestMapping(value = "/selectOne/{fileName}",method = RequestMethod.GET)
    public ApiResult selectFile(@PathVariable(value = "fileName") String fileName){
        DataSetFile dataSetFile = dataSetFileService.findDataSetFileByFileName(fileName);
        if(dataSetFile.getFileName().isEmpty()){
            return ResultUtil.error(-1,"未找到数据集");
        }
        return ResultUtil.success(dataSetFile);
    }


    //修改
    @ResponseBody
    @RequestMapping(value = {"/updateFile/{dataSetFileJson}"},method = RequestMethod.POST)
    public ApiResult updateDatasetFiles(@PathVariable(value = "dataSetFileJson") String dataSetFileJson){

        logger.info("获取数据集所选文件： ");
        DataSetFile dataSetFile = JSON.parseObject(dataSetFileJson,DataSetFile.class);
        if(dataSetFile.getFileName().isEmpty()){
            return ResultUtil.error(-1,"未找到数据集");
        }
        return ResultUtil.success();
    }


    //删除
    @ResponseBody
    @RequestMapping(value = {"/deleteFile/{deleteFilesId},{dataSetId}"},method = RequestMethod.POST)
    public ApiResult deleteFiles(@PathVariable(value = "deleteFilesId")List<Integer> filesId,
                                 @PathVariable(value = "dataSetId") int datasetId ){
        logger.info("获取数据集：");
        DataSet dataSet = dataSetService.findById(datasetId);
        if(dataSet.getDataSetName().isEmpty()){
            return ResultUtil.error(-1,"数据集不存在");
        }
        for(Integer fileID: filesId){
            DataSetFile dataSetFile = dataSetFileService.findDataSetFileById(fileID);
            if(dataSetFile.getFileName().isEmpty()){
                logger.info("此文件ID不存在 ，开始查找下一个文件ID");
                continue;
            }
            logger.info("执行删除文件"+fileID);
            dataSetFileService.deleteByFileId(fileID);
        }
        return ResultUtil.success();
    }

    /**
     * 更具选择的上传文件构建  List<DataSetFile>
     *     初步能够生成的是   文件名称（英文）
     *                        文件上传时间
     *                        文件所属数据集
     *                        所选文件大小
     *                        文件保存路径
     *
     * */
    public List<DataSetFile> packageDataSetFiles(List<String> files,DataSet dataSet)throws IOException{
        List<DataSetFile> dataSetFiles = new ArrayList<>();
        DataSetFile dataSetFile = new DataSetFile();
        for( String f :files){

            /**
             * 文件指标 判定
             * */
            dataSetFile.setFileName(f);
            long timetmp = System.currentTimeMillis();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
            dataSetFile.setOnloadTimeDate(newTime);
            dataSetFile.setDataSetId(dataSet.getId());

            String path = dataSet.getDataSetStoreUrl();
            dataSetFile.setFilePath(path);

            String filesize = GetFileSize(f);
            dataSetFile.setFileSize(filesize);

            dataSetFile.setFileSortBy(DataSetFileConsts.FILE_SORT_BY_FILENAME);
            dataSetFile.setFileSortType(DataSetFileConsts.FILE_SORT_TYPE_DESC);

            dataSetFiles.add(dataSetFile);
        }
        return dataSetFiles;
    }

    private static String GetFileSize(String Path){
        return GetFileSize(new File(Path));
    }


    private static String GetFileSize(File file){
        String size = "";
        if(file.exists() && file.isFile()){
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) +"GB";
            }
        }else if(file.exists() && file.isDirectory()){
            size = "";
        }else{
            size = "0BT";
        }
        return size;
    }

    private Sort changSortBy(String orderType, String orderColumn){
        Sort sort = new Sort(Sort.Direction.fromString(orderType),orderColumn);
        return sort;
    }

    private Sort basicSortBy(){
        return changSortBy(DataSetConsts.SORTTYPE_DESC,DataSetConsts.SORT_BY_DATASET_ENGLISH_NAME);
    }


}
