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

    public DataSystem findByDataSetId (String datasetId) throws IOException;

    public DataSystem findByDataSetName(String datasetName)throws IOException;

    public List<DataSystem> findAll(Sort sort)throws IOException;

    public void update (String en_datasetName,
                        String ch_datasetName,
                        String path,String desciption,
                        String hivetableName,
                        String datasetId) throws IOException;

    public void clearByDataSetId (String datasetId) throws IOException;

    public void deleteByDataSetId (String datasetId) throws IOException;

    public void upload (String datasetId,List<DataSetFile> fileList) throws IOException;

    public void sort (String datasetId,String sort) throws IOException;

}
