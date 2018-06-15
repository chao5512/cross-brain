package com.dataset.management.service;

import com.dataset.management.entity.DataSetFile;

import org.springframework.data.domain.Sort;

import javax.swing.plaf.PanelUI;
import java.util.List;

public interface IntDataSetFileService {
    public DataSetFile save(DataSetFile datasetFile);

    public List<DataSetFile> save (List<DataSetFile> dataSetFiles);

    public List<DataSetFile> findAll(Sort sort);

    public List<DataSetFile> findAll();

    public List<DataSetFile> findDataSetFilesByDataSetId(int datasetId);

    public List<DataSetFile> findDataSetFilesByDataSetId(int dataSetId,Sort sort);

    public DataSetFile findDataSetFileById(int datasetFileId);

    public DataSetFile findDataSetFileByFileName(String  fileName);

    public List<String> isExistsFiles(List<DataSetFile> dataSetFiles);

    public void deleteDataSetFilesByDataSetId(int datasetId);

    public void deleteById(int datasetId);

    public void deleteAll();

    public long count();

}
