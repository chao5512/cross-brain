package com.dataset.management.service;

import com.dataset.management.entity.DataSetFile;

import org.springframework.data.domain.Sort;

import java.util.List;

public interface IntDataSetFileService {
    public DataSetFile save(DataSetFile datasetFile);

    public List<DataSetFile> findAll(Sort sort);

    public DataSetFile findBydatasetFileId(String datasetFileId);

    public DataSetFile findBydatasetFileName(String datasetFileId);

    public void deleteByFileId(String datasetFileId);

    public void deleteAll();

    public long count();

    public void updateAll(String path,String datasetfileDesc,String datasetUpdateDesc,String datasetUpdateTime,String datasetId);


}
