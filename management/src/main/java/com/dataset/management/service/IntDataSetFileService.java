package com.dataset.management.service;

import com.dataset.management.entity.DataSetFile;

import org.springframework.data.domain.Sort;

import javax.swing.plaf.PanelUI;
import java.util.List;

public interface IntDataSetFileService {
    public DataSetFile save(DataSetFile datasetFile);

    public List<DataSetFile> save (List<DataSetFile> dataSetFiles);

    public List<DataSetFile> findAll(Sort sort,String datasetId);

    public DataSetFile findDataSetFileByDataSetFileName(String datasetFileName);

    public List<DataSetFile> findDataSetFilesBydatasetId(String datasetId);

    public DataSetFile findDataSetFileByDataSetFileId(String datasetFileId);

    public void deleteByFileId(String datasetFileId);

    public void deleteBydatasetId(String datasetId);

    public void deleteAll();

    public long count();

    public void updateFileDescOrFileName(String fileDesc,String fileName,String fileId);


}
