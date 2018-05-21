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

    public List<DataSetFile> findAll(Sort sort);

    public DataSetFile findBydatasetFileId(String datasetFileId);

    public DataSetFile findBydatasetFileName(String datasetFileId);

    public void deleteByFileId(String datasetFileId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSetFile dsf SET " +
            "dsf.filePath = ?1," +
            "dsf.fileDesc = ?2," +
            "dsf.fileUpdateDesc =?3," +
            "dsf.lastUpdateTime =?4 WHERE dsf.fileId = ?5")
    public void updateAll(String path,String datasetfileDesc,String datasetUpdateDesc,String datasetUpdateTime,String datasetId);
}
