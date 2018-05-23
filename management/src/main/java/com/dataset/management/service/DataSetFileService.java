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

    public DataSetFile save(DataSetFile datasetFile){
        return dataSetFileRepository.save(datasetFile);
    }

    public List<DataSetFile> findAll(Sort sort){
        return dataSetFileRepository.findAll(sort);
    }

    public DataSetFile findBydatasetFileId(String datasetFileId){
        return dataSetFileRepository.findBydatasetFileId(datasetFileId);
    }

    public DataSetFile findBydatasetFileName(String datasetFileName){
        return dataSetFileRepository.findBydatasetFileName(datasetFileName);
    }

    public void deleteByFileId(String datasetFileId){
        dataSetFileRepository.deleteByFileId(datasetFileId);
    }

    public void deleteAll(){ dataSetFileRepository.deleteAll();}

    public long count(){return  dataSetFileRepository.count();}

    public void updateAll(String path,String datasetfileDesc,String datasetUpdateDesc,String datasetUpdateTime,String datasetId){
        dataSetFileRepository.updateAll(path,datasetfileDesc,datasetUpdateDesc,datasetUpdateTime,datasetId);
    }
}
