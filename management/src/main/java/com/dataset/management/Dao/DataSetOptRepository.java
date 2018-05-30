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

    public DataSystem findById (int Id) throws IOException;

    public DataSystem findByDataSetId(int datasetId) throws IOException;

    public DataSystem findByDataSetName (String datasetName)throws IOException;

    public List<DataSystem> findByUserName(String userName);

    public List<DataSystem> findAll(Sort sort);

    public void deleteAll();

    public void deleteById (int datasetId) throws IOException;

}
