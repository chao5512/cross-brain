package com.dataset.management.Dao;

import com.dataset.management.entity.DataSetFile;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DataSetFileRepository extends JpaRepository<DataSetFile,String>{
    public DataSetFile save(DataSetFile datasetFile);

    public List<DataSetFile> save (List<DataSetFile> dataSetFiles);

    public List<DataSetFile> findAll(Sort sort);

    public List<DataSetFile> findDataSetFilesByDataSetId(int dataSetId,Sort sort);

    public List<DataSetFile> findDataSetFilesByDataSetId(int Id);

    public DataSetFile findDataSetFileById(int datasetFileId);

    public DataSetFile findDataSetFileByFileName(String  fileName);

    public void deleteById(int datasetFileId);

    public void deleteDataSetFilesByDataSetId(int datasetId);

    public void deleteAll();

    public long count();

}
