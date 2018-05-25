package com.dataset.management.service;

import com.dataset.management.entity.DataSetFile;

import org.springframework.data.domain.Sort;

import java.util.List;

public interface IntDataSetFileService {
    public DataSetFile save(DataSetFile datasetFile);

    public List<DataSetFile> save (List<DataSetFile> dataSetFiles);

    public List<DataSetFile> findAll(Sort sort);

    public DataSetFile findBydatasetFileId(String datasetFileId);

    public DataSetFile findBydatasetId(String datasetId);

    public DataSetFile findBydatasetFileName(String datasetFileId);

    public void deleteByFileId(String datasetFileId);

    public void deleteBydatasetId(String datasetId);

    public void deleteAll();

    public long count();

    public void updateAll(String path,String datasetfileDesc,String datasetUpdateDesc,String datasetUpdateTime,String datasetId);


}
