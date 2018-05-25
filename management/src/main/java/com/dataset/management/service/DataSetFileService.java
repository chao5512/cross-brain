package com.dataset.management.service;

import com.dataset.management.Dao.DataSetFileRepository;
import com.dataset.management.entity.DataSetFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSetFileService implements IntDataSetFileService {
    @Autowired
    private DataSetFileRepository dataSetFileRepository;

    //也是上传的功能
    public DataSetFile save(DataSetFile datasetFile){
        return dataSetFileRepository.save(datasetFile);
    }

    public List<DataSetFile> save (List<DataSetFile> dataSetFiles){
        return dataSetFileRepository.save(dataSetFiles);
    }

    public List<DataSetFile> findAll(Sort sort,String datasetId){
        return dataSetFileRepository.findAll(sort,datasetId);
    }

    public DataSetFile findDataSetFileByDataSetFileName(String datasetFileName){
        return dataSetFileRepository.findDataSetFileByDataSetFileName(datasetFileName);
    }

    public List<DataSetFile> findDataSetFilesBydatasetId(String datasetId){
        return dataSetFileRepository.findDataSetFilesBydatasetId(datasetId);
    }

    public void deleteByFileId(String datasetFileId){
        dataSetFileRepository.deleteByFileId(datasetFileId);
    }

    public void deleteBydatasetId(String datasetId){
        dataSetFileRepository.deleteBydatasetId(datasetId);
    }

    public void deleteAll(){ dataSetFileRepository.deleteAll();}

    public long count(){return  dataSetFileRepository.count();}

    public void updateFileDescOrFileName(String fileDesc,String fileName,String filetId){
        dataSetFileRepository.updateFileDescOrFileName(fileDesc,fileName,filetId);
    }

    public DataSetFile findDataSetFileByDataSetFileId(String datasetFileId){
        return dataSetFileRepository.findDataSetFileByDataSetFileId(datasetFileId);
    }
}
