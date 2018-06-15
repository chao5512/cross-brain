package com.dataset.management.service;

import com.dataset.management.PrimaryDao.DataSetFileRepository;
import com.dataset.management.consts.DataSetFileConsts;
import com.dataset.management.entity.DataSetFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataSetFileService implements IntDataSetFileService {
    @Autowired
    private DataSetFileRepository dataSetFileRepository;
    private static Logger logger = LoggerFactory.getLogger(DataSetFileService.class);

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

    @Override
    public List<DataSetFile> findAll(){
        Sort sort = new Sort(Sort.Direction.fromString(DataSetFileConsts.FILE_SORT_TYPE_ASC),DataSetFileConsts.FILE_SORT_BY_FILENAME);
        return findAll(sort);
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
    public List<String> isExistsFiles(List<DataSetFile> dataSetFiles){
        List<String> filelist = new ArrayList<>();
        for (DataSetFile file: dataSetFiles){
            String name = file.getFileName();
            logger.info("当前文件名称："+name);
            if(!filelist.contains(file)){
                filelist.add(name);
            }
        }
        return filelist;
    }

    @Transactional
    public void deleteDataSetFilesByDataSetId(int datasetId){
        dataSetFileRepository.deleteDataSetFilesByDataSetId(datasetId);
    }
    @Transactional
    public void deleteById(int datasetId){
        dataSetFileRepository.deleteById(datasetId);
    }
    @Transactional
    public void deleteAll(){ dataSetFileRepository.deleteAll();}

    public long count(){return  dataSetFileRepository.count();}

}
