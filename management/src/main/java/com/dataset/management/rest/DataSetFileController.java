package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.consts.DataSetConsts;
import com.dataset.management.consts.DataSetFileConsts;
import com.dataset.management.consts.DataSetSystemConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.entity.DataSystem;
import com.dataset.management.service.HdfsService;
import com.dataset.management.service.IntDataSetFileService;
import com.dataset.management.service.IntDataSetOptService;
import com.dataset.management.service.IntDataSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/dataset")
@CrossOrigin
public class DataSetFileController {
    @Autowired
    IntDataSetService dataSetService;

    @Autowired
    IntDataSetFileService dataSetFileService;

    @Autowired
    IntDataSetOptService dataSetOptService;

    @Autowired
    HdfsService hdfsService;


    private static Logger logger = LoggerFactory.getLogger(DataSetFileController.class);

    //上传
    @ResponseBody
    @RequestMapping(value = {"/upload/","/uploadFiles"},method = RequestMethod.POST)
    public ApiResult uploadFilestoDataSet(@RequestParam(value = "files") List<String> filesName,
                                          @RequestParam(value = "fileSortBy") String  sortBy,
                                          @RequestParam(value = "fileSortType") String  sortType,
                                          String datasetId) throws IOException{
        int code =0;
        String message ="上传数据集文件信息：";
        Sort sort;
        logger.info("修改当前数据集上传文件状态：");
        dataSetService.updateDataSetUploadStatus(DataSetConsts.UPLOAD_STATUS_LOADING,datasetId);

        List<DataSetFile> dataSetFiles = packageDataSetFiles(filesName,datasetId);
        List<DataSetFile> dataSetFileList = dataSetFileService.save(dataSetFiles);
        dataSetService.updateDataSetFilecount(dataSetFiles.size(),datasetId);
        logger.info(" 准备 上传至hdfs");
        String hdfsurl = hdfsService.conterHdfsUrl();
        logger.info("hdfs 路径："+ hdfsurl);
        hdfsService.uploadFiles(filesName,hdfsurl);
        logger.info("上传完毕，数据集状态更改");
        dataSetService.updateDataSetUploadStatus(DataSetConsts.UPLOAD_STATUS_COMPLETE,datasetId);


        if( sortBy != null && sortType != null){
             sort = changSortBy(sortType,sortBy);
        }
        logger.info("数据集文件排序方式："+sortBy+"  "+sortType);
        sort = basicSortBy();
        logger.info("查看上传后数据集文件的列表：");
        List<DataSetFile> fileList = dataSetFileService.findAll(sort,datasetId);
        return ResultUtil.success(fileList);
    }

    //查询
    @ResponseBody
    @RequestMapping(value = {"/listFiles/","/listFiles"},method = RequestMethod.POST)
    public ApiResult selectAllFiles(String sortBy,String sortType,String datasetId){
        int code = 0;
        String message =" 查询数据集文件信息：";
        Sort sort;

        if( sortBy != null && sortType != null){
            sort = changSortBy(sortType,sortBy);
        }
        logger.info("数据集文件排序方式："+sortBy+"  "+sortType);
        sort = basicSortBy();
        logger.info("查看上传后数据集文件的列表：");
        List<DataSetFile> fileList = dataSetFileService.findAll(sort,datasetId);
        return ResultUtil.success(fileList);
    }

    //修改
    @ResponseBody
    @RequestMapping(value = {"/updateFile/","/updateFile"},method = RequestMethod.POST)
    public ApiResult updateDatasetFiles(@RequestParam(value = "datasetFileBasicDesc") String fileBasicDesc,
                                        @RequestParam(value = "datasetFileName") String fileName,
                                        String fileId){
        int code = 0;
        String message ="修改数据集文件信息";
        logger.info("获取数据集所选文件： ");
        DataSetFile dataSetFile = dataSetFileService.findDataSetFileByDataSetFileId(fileId);
        if(fileBasicDesc != null){
            dataSetFile.setFileDesc(fileBasicDesc);
        }
        if(fileName != null){
            dataSetFile.setFileName(fileName);
        }
        dataSetFileService.updateFileDescOrFileName(
                dataSetFile.getFileDesc(),
                dataSetFile.getFileName(),
                fileId);

        return ResultUtil.success();
    }


    //删除

    /**
     * 更具选择的上传文件构建  List<DataSetFile>
     *     初步能够生成的是   文件名称（英文）
     *                        文件上传时间   ---------生成 fileID
     *                        文件所属数据集
     *                        所选文件大小
     *                        文件保存路径
     *
     * */
    public List<DataSetFile> packageDataSetFiles(List<String> files,String datasetId)throws IOException{
        List<DataSetFile> dataSetFiles = new ArrayList<>();
        DataSetFile dataSetFile = new DataSetFile();
        for( String f :files){

            /**
             * 文件指标 判定
             * */
            dataSetFile.setFileName(f);

            Long newTimetmp = dataSetFile.getOnloadTimetmp();
            dataSetFile.setOnloadTimeDate(newTimetmp);

            byte[] nameByte = f.getBytes("UTF-8");
            String fileId = nameByte+"_"+newTimetmp;
            dataSetFile.setFileId(fileId);

            dataSetFile.setBpodataSets(datasetId);

            DataSet dataSet = dataSetService.findByDataSetId(datasetId);
            String path = dataSet.getDataSetStoreUrl();
            dataSetFile.setFilePath(path);

            String filesize = GetFileSize(f);
            dataSetFile.setFileSize(filesize);

            dataSetFile.setFileSortBy(DataSetFileConsts.FILE_SORT_BY_FILENAME);
            dataSetFile.setFileSortType(DataSetFileConsts.FILE_SORT_TYPE_AESC);

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
