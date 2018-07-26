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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "数据集文件管理",description = "数据集管理API")
@Controller
@RequestMapping("datasetFile")
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
    @ApiOperation(value = "上传文件",httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public ApiResult uploadFilestoDataSet(@RequestParam(value = "files")MultipartFile[]  multipartFiles,
                                          @RequestParam(value = "datasetId")int dataSetId) throws IOException,Exception{
        DataSet contentdDataSet;
        DataSetFile cntentDataSetFile;
        BufferedOutputStream outputStream;
        int upload =0;

        contentdDataSet = dataSetService.findById(dataSetId);

        int count = contentdDataSet.getDataSetFileCount();
        logger.info("修改当前数据集 "+contentdDataSet+"   上传文件状态：");
        contentdDataSet.setDataSetStatus(DataSetConsts.UPLOAD_STATUS_LOADING);
        dataSetService.save(contentdDataSet);

        //获取数据集存储路径
        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();

        String dataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConsts.DATASET_STOREURL_DIR
                +"/"+contentdDataSet.getUserId()+"/"+contentdDataSet.getDataSetName();
        //hdfs:8.8.8.8:900/DATASETSYSTEM/user/datasetName

        logger.info("数据集当前根目录："+dataStoreUrl);

        String tmpPath = DataSetConsts.DATASET_STOREURL_DIR
                +"/"+contentdDataSet.getUserId()+"/"+contentdDataSet.getDataSetName();
        logger.info("hdfs中数据集路径："+tmpPath);
        //校验文件是否存在
        List<DataSetFile> estsDataSetFiles = dataSetFileService.findDataSetFilesByDataSetId(dataSetId);
        logger.info("获取当前数据集中已经存在的文件列表："+estsDataSetFiles);

        //已经存在的数据集中文件名称：
        List<String> estsFilesNmaes = isExistsFiles(estsDataSetFiles);
        logger.info("当前数据集存在文件名称列表："+estsDataSetFiles);
        logger.info("上传数据集文件列表：");

        if(multipartFiles.length<=0){
            return ResultUtil.error(-1,"没有获取到文件");
        }
        for(int i=0;i<multipartFiles.length;i++){
            //遍历每一个媒体文件
            MultipartFile setFile = multipartFiles[i];
            //路径还是文件名称？？？
            String name = setFile.getOriginalFilename();
            logger.info("看看是路径还是文件名："+name);
            String nameTRUE = name.substring(name.lastIndexOf("/")+1,name.length());
            logger.info("字段切分后的文件名："+nameTRUE);

            //不存在的话 就准备上传
            if(!estsFilesNmaes.contains(name)){
                cntentDataSetFile = new DataSetFile();

                long timetmp = System.currentTimeMillis();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));

                //构造 DatasetFile
                cntentDataSetFile.setFilePath(tmpPath);
                cntentDataSetFile.setFileDesc(null);
                cntentDataSetFile.setFileSortType(DataSetFileConsts.FILE_SORT_BY_FILENAME);
                cntentDataSetFile.setFileSortBy(DataSetFileConsts.FILE_SORT_TYPE_ASC);
                cntentDataSetFile.setFileName(name);
                File file = new File(name);
                String filesize = GetFileSize(file);
                cntentDataSetFile.setOnloadTimeDate(newTime);
                cntentDataSetFile.setFileSize(filesize);
                cntentDataSetFile.setDataSetId(dataSetId);

                outputStream = new BufferedOutputStream(
                        new FileOutputStream(
                                new File(name)
                        )
                );
                outputStream.write(setFile.getBytes());
                outputStream.flush();
                logger.info("准备hdfs  上传数据文件");
                hdfsService.copyFileToHDFS(name,tmpPath);

                logger.info("数据库中添加文件信息——");
                estsFilesNmaes.add(name);
                dataSetFileService.save(cntentDataSetFile);
                upload = upload+1;
                outputStream.close();
            }
        }
        List<DataSetFile> newDatasetFiles = dataSetFileService.findDataSetFilesByDataSetId(dataSetId);
        //修改数据集必要参数
        logger.info("上传完毕，数据集状态更改");
        contentdDataSet.setDataSetStatus(DataSetConsts.UPLOAD_STATUS_COMPLETE);
        logger.info("修改时对应数据集上文件数：");
        int newCount = count + upload;
        contentdDataSet.setDataSetFileCount(newCount);
        contentdDataSet.setDataSetUpdateDesc("upload the files ");

        long timetmp = System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
        contentdDataSet.setDataSetLastUpdateTime(newTime);
        dataSetService.save(contentdDataSet);
        return ResultUtil.success(newDatasetFiles);
    }

    //查询
    @ApiOperation(value = "依据指定的排序方式查询当前用户下的所有数据集文件",httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = {"/listFileAll/{filesSortBy}/{filesSortType}/{dataSetId}"},method = RequestMethod.GET)
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
        for(DataSetFile dataSetFile:fileList){
            logger.info("更改数据库中字段排序方式");
            dataSetFile.setFileSortBy(sortBy);
            dataSetFile.setFileSortType(sortType);
            dataSetFileService.save(dataSetFile);
        }
        if(fileList.isEmpty()){
            return ResultUtil.error(-1,"没有文件");
        }
        return ResultUtil.success(fileList);
    }

//    //查询  fileName
////    @ApiOperation(value = "依据指定文件名称查询文件",httpMethod = "GET")
//    @ResponseBody
//    @RequestMapping(value = "/selectByFileName/{fileName}",method = RequestMethod.GET)
//    public ApiResult selectFileByFileName(@PathVariable(value = "fileName") String fileName){
//        DataSetFile dataSetFile = dataSetFileService.findDataSetFileByFileName(fileName);
//        if(dataSetFile.getFileName().isEmpty()){
//            return ResultUtil.error(-1,"未找到数据集");
//        }
//        return ResultUtil.success(dataSetFile);
//    }


//    //查询  fileId
////    @ApiOperation(value = "依据指定文件Id 查询文件",httpMethod = "GET")
//    @ResponseBody
//    @RequestMapping(value = "/selectByFileId/{fileId}",method = RequestMethod.GET)
//    public ApiResult selectFileByFileId(@PathVariable(value = "fileId") int fileId){
//        DataSetFile dataSetFile = dataSetFileService.findDataSetFileById(fileId);
//        if(dataSetFile.getFileName().isEmpty()){
//            return ResultUtil.error(-1,"未找到数据集");
//        }
//        return ResultUtil.success(dataSetFile);
//    }


    //修改
    /**
     {"id":47,"fileName":"files","dataSetId":23,"filePath":"sss","fileSortBy":"sss","fileSortType":"ddd","fileDesc":"upload  success! ",
     "onloadTimedate":"2018-06-01 13:00:14","fileSize":"ddd"}
     * */
//    @ApiOperation(value = "依据客户端指定的文件属性，修改文件",httpMethod = "POST")
//    @ResponseBody
//    @RequestMapping(value = {"/updateFile"},method = RequestMethod.POST)
//    public ApiResult updateDatasetFiles(@RequestParam(value = "dataSetFileJson") String dataSetFileJson){
//
//        logger.info("获取数据集所选文件 ");
//        DataSetFile dataSetFile = JSON.parseObject(dataSetFileJson,DataSetFile.class);
//        if(dataSetFile.getFileName().isEmpty()){
//            return ResultUtil.error(-1,"数据集中没有此文件名称");
//        }
//        logger.info("当前文件ID：名称："+dataSetFile.getId()+"  "+dataSetFile.getFileName());
//        DataSetFile newDataSetFile = dataSetFileService.save(dataSetFile);
//
//        long timetmp = System.currentTimeMillis();
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
//        DataSet dataSet = dataSetService.findById(dataSetFile.getDataSetId());
//        dataSet.setDataSetLastUpdateTime(newTime);
//        dataSet.setDataSetUpdateDesc("update the file :"+dataSetFile.getFileName());
//        dataSetService.save(dataSet);
//
//        return ResultUtil.success(newDataSetFile);
//    }


    //删除
    @ApiOperation(value = "依据指定文件Id 删除文件",httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = {"/deleteSomeFiles/{deleteFileId}"},method = RequestMethod.POST)
    public ApiResult deleteFiles(@PathVariable(value = "deleteFileId") int fileId){
        logger.info("获取数据集：");
        DataSetFile dataSetFile = dataSetFileService.findDataSetFileById(fileId);
            if(dataSetFile.getFileName().isEmpty() || dataSetFile.getId() == 0){
                logger.info("数据集中没有文件："+dataSetFile.getFileName());
            }else {
                logger.info("执行删除文件："+dataSetFile.getFileName());
                String datasetFilePath = dataSetFile.getFilePath();
                String hdfsfilepath = datasetFilePath+ dataSetFile.getFileName();
                hdfsService.deletedir(hdfsfilepath);
                dataSetFileService.deleteById(dataSetFile.getId());
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
