package com.dataset.management.PrimaryDao;

import com.dataset.management.entity.DataSetFile;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

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
