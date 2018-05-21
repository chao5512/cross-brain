package com.dataset.management.Dao;

import com.dataset.management.entity.DataSystem;

import com.dataset.management.entity.DataSetFile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

public interface DataSetOptRepository extends JpaRepository <DataSystem,String>{

    public DataSystem save (DataSystem dataSystem);

    public DataSystem findByDataSetId (String datasetId) throws IOException;

    public DataSystem findByDataSetName (String datasetName)throws IOException;

    public List<DataSystem> findAll();

    public List<DataSystem> findAll(Sort sort);

    @Modifying
    @Transactional
    @Query(value = "UPDATE DataSystem dsst SET " +
            "dsst.datasetName = ?1," +
            "dsst.datasetEnglishName = ?2," +
            "dsst.datasetStoreurl = ?3," +
            "dsst.datasetDesc = ?4," +
            "dsst.datasetHiveTablename = ?5 WHERE dsst.dataSetId= ?6")
    public void update (String en_datasetName,String ch_datasetName,String path,String desciption,String hivetableId,String datasetId) throws IOException;

    public void deleteAll();

    public void clearByDataSetId (String datasetId) throws IOException;

    public void deleteByDataSetId (String datasetId) throws IOException;

    public void upload (String datasetId,List<DataSetFile> fileList) throws IOException;

    public void sort (String datasetId,String Sort) throws IOException;
}
