package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.consts.DataSetSystemConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.entity.DataSystem;
import com.dataset.management.service.IntDataSetFileService;
import com.dataset.management.service.IntDataSetOptService;
import com.dataset.management.service.IntDataSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.image.ImagingOpException;
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

    private static Logger logger = LoggerFactory.getLogger(DataSetFileController.class);

    //上传
    @ResponseBody
    @RequestMapping(value = {"/upload/","/uploadFiles"},method = RequestMethod.POST)
    public ApiResult uploadFilestoDataSet(@RequestParam(value = "files") List<String> files,
                                          String datasetId) throws IOException{
        int code =0;
        String message ="上传数据集文件信息：";

        List<DataSetFile> dataSetFiles = packageDataSetFiles(files,datasetId);

        List<DataSetFile> dataSetFileList = dataSetFileService.save(dataSetFiles);
        return new ApiResult(code,dataSetFileList,message);

    }
    //查询


    //修改
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

            String fileId = dataSetFile.getFileName()+"_"+newTimetmp;
            dataSetFile.setFileId(fileId);

            dataSetFile.setBpodataSets(datasetId);

            DataSystem dataSystem = dataSetOptService.findByDataSetId(datasetId);
            String path = dataSystem.getDatasetStoreurl();
            dataSetFile.setFilePath(path);

            String filesize = GetFileSize(f);
            dataSetFile.setFileSize(filesize);

            dataSetFiles.add(dataSetFile);
        }
        return dataSetFiles;
    }

    public static String GetFileSize(String Path){
        return GetFileSize(new File(Path));
    }


    public static String GetFileSize(File file){
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

}
