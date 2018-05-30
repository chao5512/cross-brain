package com.dataset.management.service;

import com.dataset.management.Dao.DataSetFileRepository;
import com.dataset.management.entity.DataSetFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<DataSetFile> findAll(Sort sort){
        return dataSetFileRepository.findAll(sort);
    }

    public List<DataSetFile> findDataSetFilesByDataSetId(int datasetId){
        return dataSetFileRepository.findDataSetFilesByDataSetId(datasetId);
    }
    public List<DataSetFile> findDataSetFilesByDataSetId(int dataSetId,Sort sort){
        return dataSetFileRepository.findDataSetFilesByDataSetId(dataSetId,sort);
    }

    public DataSetFile findDataSetFileById(int datasetFileId){
        return dataSetFileRepository.findDataSetFileById(datasetFileId);
    }
    public DataSetFile findDataSetFileByFileName(String  fileName){
        return dataSetFileRepository.findDataSetFileByFileName(fileName);
    }
    @Override
    public List<String> isExistsFiles(List<DataSetFile> dataSetFiles){
        List<String> filelist = new ArrayList<>();
        for (DataSetFile file: dataSetFiles){
            String name = file.getFileName();
            if(!filelist.contains(file)){
                filelist.add(name);
            }
        }
        return filelist;
    }

    public void deleteByFileId(int datasetFileId){
        dataSetFileRepository.deleteById(datasetFileId);
    }

    public void deleteDataSetFilesByDataSetId(int datasetId){
        dataSetFileRepository.deleteDataSetFilesByDataSetId(datasetId);
    }

    public void deleteById(int datasetId){
        dataSetFileRepository.deleteById(datasetId);
    }

    public void deleteAll(){ dataSetFileRepository.deleteAll();}

    public long count(){return  dataSetFileRepository.count();}

}
