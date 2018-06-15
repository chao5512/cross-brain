package com.dataset.management.rest;

import com.alibaba.fastjson.JSON;
import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.config.HdfsConfig;
import com.dataset.management.consts.DataSetConsts;
import com.dataset.management.consts.DataSetFileConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
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

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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

    @Autowired
    HdfsService hdfsService;

    @Autowired
    private HdfsConfig hdfsConfig;


    private static Logger logger = LoggerFactory.getLogger(DataSetFileController.class);

    //上传
    /**
     [{"fileName":"new","dataSetId":23,"filePath":"sss","fileSortBy":"sss","fileSortType":"ddd","fileDesc":"ss","fileSize":"ddd","onloadTimedate":"sss"},
     {"fileName":"files","dataSetId":23,"filePath":"sss","fileSortBy":"sss","fileSortType":"ddd","fileDesc":"ss","fileSize":"ddd","onloadTimedate":"sss"}]

     * */
    @ResponseBody
    @RequestMapping(value = "/upload/{filesJson}/{datasetId}",method = RequestMethod.POST)
    public ApiResult uploadFilestoDataSet(@PathVariable(value = "filesJson") String filesJson,
                                          @PathVariable(value = "datasetId")int dataSetId) throws IOException,Exception{


        DataSet contentdDataSet;
        DataSetFile cntentDataSetFile;
        List<String> addFileName = new ArrayList<>();

        List<DataSetFile> dataSetFiles = packageDataSetFiles(filesJson);

        contentdDataSet = dataSetService.findById(dataSetId);

        int count = contentdDataSet.getDataSetFileCount();
        logger.info("修改当前数据集 "+contentdDataSet+"   上传文件状态：");
        contentdDataSet.setDataSetStatus(DataSetConsts.UPLOAD_STATUS_LOADING);
        dataSetService.save(contentdDataSet);

        //获取数据集存储路径
        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();

        String dataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConsts.DATASET_STOREURL_DIR
                +"/"+contentdDataSet.getUserName()+"/"+contentdDataSet.getDataSetName();
        logger.info("数据集当前根目录："+dataStoreUrl);

        String tmpPath = DataSetConsts.DATASET_STOREURL_DIR
                +"/"+contentdDataSet.getUserName()+"/"+contentdDataSet.getDataSetName();

        List<DataSetFile> estsDataSetFiles = dataSetFileService.findDataSetFilesByDataSetId(dataSetId);
        logger.info("获取当前数据集中已经存在的文件名称列表："+estsDataSetFiles);

        List<String> estsFilesNmaes = isExistsFiles(estsDataSetFiles);
        logger.info("当前数据集存在文件列表："+estsDataSetFiles);

        logger.info("上传数据集文件列表：");
        for(DataSetFile setFile: dataSetFiles){
            String name = setFile.getFileName();
            //hdfs 中的全路径
            String fileHdfsPath = tmpPath+"/"+name;

            long timetmp = System.currentTimeMillis();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));

            logger.info("修改文件的默认属性：");
            setFile.setOnloadTimeDate(newTime);
            setFile.setFileDesc("upload  success! ");
            if(!estsFilesNmaes.contains(name)){
                setFile.setDataSetId(dataSetId);
                // 文件的当前根目录
                setFile.setFilePath(dataStoreUrl);
                cntentDataSetFile = dataSetFileService.save(setFile);
                logger.info("上传后的文件属性"+cntentDataSetFile);
                addFileName.add(setFile.getFileName());
                /**
                 * 文件路径应该是，但是如何获取？？
                 * */
                logger.info("远程 hdfs  文件上传中");
//                hdfsService.copyFileToHDFS(name,fileHdfsPath);

            }else {
                logger.info("改文件已经在数据库中，请重新命名后重新上传: "+setFile.getFileName());
            }
        }

        //修改数据集必要参数
        logger.info("上传完毕，数据集状态更改");
        contentdDataSet.setDataSetStatus(DataSetConsts.UPLOAD_STATUS_COMPLETE);
        logger.info("修改时对应数据集上文件数：");
        int newCount = count+dataSetFiles.size();
        contentdDataSet.setDataSetFileCount(newCount);
        contentdDataSet.setDataSetUpdateDesc("upload the file :"+addFileName);

        long timetmp = System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
        contentdDataSet.setDataSetLastUpdateTime(newTime);
        dataSetService.save(contentdDataSet);

        return ResultUtil.success("upload  success");
    }

    //查询
    @ResponseBody
    @RequestMapping(value = {"/listFileAll/{filesSortBy},{filesSortType},{dataSetId}"},method = RequestMethod.GET)
    public ApiResult selectAllFiles(@PathVariable(value = "filesSortBy") String sortBy,
                                    @PathVariable(value = "filesSortType") String sortType,
                                    @PathVariable(value = "dataSetId") int dataSetId){
        Sort sort;

        //设置文件排序方式
        if(!sortBy.equals(DataSetFileConsts.FILE_SORT_BY_FILENAME) &&
                !sortBy.equals(DataSetFileConsts.FILE_SORT_BY_UPLOADTIME)){
            logger.info("获取指定的排序字段"+sortBy);
            return ResultUtil.error(-1,"排序字段不符合规则");
        }
        if(!sortType.equals(DataSetFileConsts.FILE_SORT_TYPE_ASC) &&
                !sortType.equals(DataSetFileConsts.FILE_SORT_TYPE_DESC)){
            return ResultUtil.error(-1,"排序方式不符合规则");
        }
        if( !sortBy.equals(DataSetFileConsts.FILE_SORT_BY_FILENAME) && !sortType.equals(DataSetFileConsts.FILE_SORT_TYPE_ASC)){
            sort = changSortBy(sortType,sortBy);
            logger.info("数据集文件排序方式变更："+sortBy+"   "+sortType);
        }
        logger.info("数据集文件排序方式："+sortBy+"  "+sortType);
        sort = basicSortBy();
        logger.info("查看数据集文件的列表：");
        List<DataSetFile> fileList = dataSetFileService.findDataSetFilesByDataSetId(dataSetId,sort);
        return ResultUtil.success(fileList);
    }

    //查询  fileName
    @ResponseBody
    @RequestMapping(value = "/selectByFileName/{fileName}",method = RequestMethod.GET)
    public ApiResult selectFileByFileName(@PathVariable(value = "fileName") String fileName){
        DataSetFile dataSetFile = dataSetFileService.findDataSetFileByFileName(fileName);
        if(dataSetFile.getFileName().isEmpty()){
            return ResultUtil.error(-1,"未找到数据集");
        }
        return ResultUtil.success(dataSetFile);
    }

    //查询  fileId
    @ResponseBody
    @RequestMapping(value = "/selectByFileId/{fileId}",method = RequestMethod.GET)
    public ApiResult selectFileByFileId(@PathVariable(value = "fileId") int fileId){
        DataSetFile dataSetFile = dataSetFileService.findDataSetFileById(fileId);
        if(dataSetFile.getFileName().isEmpty()){
            return ResultUtil.error(-1,"未找到数据集");
        }
        return ResultUtil.success(dataSetFile);
    }


    //修改
    /**
     {"id":47,"fileName":"files","dataSetId":23,"filePath":"sss","fileSortBy":"sss","fileSortType":"ddd","fileDesc":"upload  success! ",
     "onloadTimedate":"2018-06-01 13:00:14","fileSize":"ddd"}
     * */
    @ResponseBody
    @RequestMapping(value = {"/updateFile/{dataSetFileJson}"},method = RequestMethod.POST)
    public ApiResult updateDatasetFiles(@PathVariable(value = "dataSetFileJson") String dataSetFileJson){

        logger.info("获取数据集所选文件 ");
        DataSetFile dataSetFile = JSON.parseObject(dataSetFileJson,DataSetFile.class);
        if(dataSetFile.getFileName().isEmpty()){
            return ResultUtil.error(-1,"数据集中没有此文件名称");
        }
        logger.info("当前文件ID：名称："+dataSetFile.getId()+"  "+dataSetFile.getFileName());
        DataSetFile newDataSetFile = dataSetFileService.save(dataSetFile);

        long timetmp = System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
        DataSet dataSet = dataSetService.findById(dataSetFile.getDataSetId());
        dataSet.setDataSetLastUpdateTime(newTime);
        dataSet.setDataSetUpdateDesc("update the file :"+dataSetFile.getFileName());
        dataSetService.save(dataSet);

        return ResultUtil.success(newDataSetFile);
    }


    //删除  多个
    @ResponseBody
    @RequestMapping(value = {"/deleteSomeFiles/{deleteFilesIdJson}"},method = RequestMethod.POST)
    public ApiResult deleteFiles(@PathVariable(value = "deleteFilesIdJson") String filesJson){
        logger.info("获取数据集：");
        List<DataSetFile> dataSetFileList = JSON.parseArray(filesJson,DataSetFile.class);
        for(DataSetFile dataSetFile:dataSetFileList){
            if(dataSetFile.getFileName().isEmpty() || dataSetFile.getId() == 0){
                logger.info("数据集中没有文件："+dataSetFile.getFileName());
            }else {
                logger.info("执行删除文件："+dataSetFile.getFileName());
                String datasetFilePath = dataSetFile.getFilePath();
                String hdfsfilepath = datasetFilePath+ dataSetFile.getFileName();
                hdfsService.deletedir(hdfsfilepath);
                dataSetFileService.deleteById(dataSetFile.getId());
            }
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
     * */
    public List<DataSetFile> packageDataSetFiles(String filesJson)throws IOException{
        List<DataSetFile> dataSetFiles = JSON.parseArray(filesJson,DataSetFile.class);
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
        return new Sort(Sort.Direction.fromString(DataSetFileConsts.FILE_SORT_TYPE_ASC),DataSetFileConsts.FILE_SORT_BY_FILENAME);
    }

    private List<String> isExistsFiles(List<DataSetFile> dataSetFiles){
        List<String> filelist = new ArrayList<>();
        for (DataSetFile file: dataSetFiles){
            String name = file.getFileName();
            logger.info("获取当前文件名称："+name);
            if(!filelist.contains(file)){
                filelist.add(name);
            }
        }
        return filelist;
    }

}
