package com.dataset.management.Dao;

import com.dataset.management.entity.DataSetFile;
import com.sun.tools.doclets.formats.html.PackageUseWriter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DataSetFileRepository extends JpaRepository<DataSetFile,String>{
    public DataSetFile save(DataSetFile datasetFile);

    public List<DataSetFile> save (List<DataSetFile> dataSetFiles);

    public List<DataSetFile> findAll(Sort sort,String datasetId);

    public List<DataSetFile> findDataSetFilesBydatasetId(String datasetId);

    public DataSetFile findDataSetFileByDataSetFileName(String datasetFileName);

    public void deleteByFileId(String datasetFileId);

    public void deleteBydatasetId(String datasetId);

    public void deleteAll();

    public long count();

    @Modifying
    @Transactional
    @Query(value = "Update DataSetFile dsf SET " +
            "dsf.fileDesc = ?1," +
            "dsf.fileName = ?2 where dsf.fileId = ?3")
    public void updateFileDescOrFileName(String fileDesc,String fileName,String filetId);

    public DataSetFile findDataSetFileByDataSetFileId(String datasetFileId);
}
