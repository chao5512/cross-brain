package com.dataset.management.controller;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.config.HdfsConfig;
import com.dataset.management.constant.DataSetConstants;
import com.dataset.management.constant.DataSetFileConstants;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.service.HdfsService;
import com.dataset.management.service.IntDataSetFileService;
import com.dataset.management.service.IntDataSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
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

        try {
            contentdDataSet = dataSetService.findById(dataSetId);

            int count = contentdDataSet.getDataSetFileCount();
            logger.info("修改当前数据集 "+contentdDataSet+"   上传文件状态：");
            contentdDataSet.setDataSetStatus(DataSetConstants.UPLOAD_STATUS_LOADING);
            dataSetService.save(contentdDataSet);

            //获取数据集存储路径
            String hdfsUrl = hdfsConfig.getHdfsUrl();
            Long hdfsPort = hdfsConfig.getHdfsProt();

            String dataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConstants.DATASET_STOREURL_DIR
                    +"/"+contentdDataSet.getUserId()+"/"+contentdDataSet.getDataSetName();
            //hdfs:8.8.8.8:900/DATASETSYSTEM/user/datasetName

            logger.info("数据集当前根目录："+dataStoreUrl);

            String tmpPath = DataSetConstants.DATASET_STOREURL_DIR
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
                return ResultUtil.error(2011,"没有获取到文件");
            }
            for(int i=0;i<multipartFiles.length;i++){
                //遍历每一个媒体文件
                MultipartFile setFile = multipartFiles[i];
                //路径还是文件名称？？？
                String name = setFile.getOriginalFilename();
                logger.info("路径还是文件名："+name);
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
                    cntentDataSetFile.setFileName(name);
                    File file = new File(name);
                    String filesize = hdfsService.GetFileSize(file);
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
            contentdDataSet.setDataSetStatus(DataSetConstants.UPLOAD_STATUS_COMPLETE);
            logger.info("修改时对应数据集上文件数：");
            int newCount = count + upload;
            contentdDataSet.setDataSetFileCount(newCount);

            //数据集文件夹大小
            String dirsize = hdfsService.GetFileSize(dataStoreUrl);
            contentdDataSet.setDataSetSize(dirsize);

            long timetmp = System.currentTimeMillis();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String newTime = sdf.format(new Date(Long.parseLong(String.valueOf(timetmp))));
            contentdDataSet.setDataSetLastUpdateTime(newTime);
            dataSetService.save(contentdDataSet);
            return ResultUtil.success(newDatasetFiles);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("文件上传失败");
            return ResultUtil.error(2011,"文件上传失败");
        }
    }

    //查询
    @ApiOperation(value = "依据指定的排序方式查询当前用户下的所有数据集文件",httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = {"/listFileAll"},method = RequestMethod.GET)
    public ApiResult selectAllFiles(@RequestParam(value = "dataSetId") int dataSetId){
        Sort sort;

        sort = basicSortBy();

        logger.info("查看数据集文件的列表：");
        try {
            List<DataSetFile> fileList = dataSetFileService.findDataSetFilesByDataSetId(dataSetId,sort);
            for(DataSetFile dataSetFile:fileList){
                logger.info("更改数据库中字段排序方式");
                dataSetFileService.save(dataSetFile);
            }
            if(fileList.isEmpty()){
                return ResultUtil.error(2012,"没有文件");
            }
            return ResultUtil.success(fileList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("文件查询操作失败");
            return ResultUtil.error(2012,"文件查询操作失败");
        }
    }



    //删除
    @ApiOperation(value = "依据指定文件Id 删除文件",httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = {"/deleteSomeFiles"},method = RequestMethod.POST)
    public ApiResult deleteFiles(@RequestParam(value = "deleteFileId") int fileId){
        logger.info("获取数据集：");
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除失败");
            return ResultUtil.error(2013,"删除失败");
        }
    }

    /**
     * 更具选择的上传文件构建  List<DataSetFile>
     *     初步能够生成的是   文件名称（英文）/文件上传时间 /文件所属数据集 / 所选文件大小 / 文件保存路径
     * */

    private Sort changSortBy(String orderType, String orderColumn){
        Sort sort = new Sort(Sort.Direction.fromString(orderType),orderColumn);
        return sort;
    }

    private Sort basicSortBy(){
        List<Sort.Order> sortList = new ArrayList<Sort.Order>();
        Sort.Order datasetFileNameSortBy = new Sort.Order(Sort.Direction.DESC, DataSetFileConstants.FILE_SORT_BY_FILENAME);
        Sort.Order datasetFileUpdateTime = new Sort.Order(Sort.Direction.DESC,DataSetFileConstants.FILE_SORT_BY_UPLOADTIME);
        sortList.add(datasetFileNameSortBy);
        sortList.add(datasetFileUpdateTime);
        return new Sort(sortList);
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
