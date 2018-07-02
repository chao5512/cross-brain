package com.dataset.management.service;

import com.dataset.management.entity.DataSystem;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.DataSetFile;
import org.springframework.data.domain.Sort;
import sun.awt.SunHints;

import java.io.IOException;
import java.util.List;

public interface IntDataSetOptService {

    public DataSystem save (DataSystem dataSystem) throws IOException;

    public DataSystem findById (int datasetId) throws IOException;

    public DataSystem findByDataSetId(int datasetId) throws IOException;

    public List<DataSystem> findByDataSetName(String datasetName)throws IOException;

    public List<DataSystem> findByUserId(int userId);

    public List<DataSystem> findAll(Sort sort)throws IOException;

    public void deleteById (int datasetId) throws IOException;

    public void deleteAll();


}
