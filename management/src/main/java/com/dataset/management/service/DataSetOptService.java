package com.dataset.management.service;

import com.dataset.management.Dao.DataSetOptRepository;
import com.dataset.management.entity.DataSystem;
import com.dataset.management.entity.DataSetFile;
import com.dataset.management.entity.Hiveinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DataSetOptService implements IntDataSetOptService {

    private Logger logger = LoggerFactory.getLogger(DataSetOptService.class);

    @Autowired
    private DataSetOptRepository dataSetOptRepository;

    @Override
    public DataSystem save(DataSystem dataSystem) throws IOException{
        return dataSetOptRepository.save(dataSystem);
    }

    @Override
    public DataSystem findByDataSetId(String datasetId)throws IOException{
        return dataSetOptRepository.findByDataSetId(datasetId);
    }

    @Override
    public DataSystem findByDataSetName(String datasetName)throws IOException{
        return dataSetOptRepository.findByDataSetName(datasetName);
    }

    @Override
    public List<DataSystem> findAll(Sort sort)throws IOException{
        return dataSetOptRepository.findAll(sort);
    }

    @Override
    public void update (String en_datasetName,
                        String ch_datasetName,
                        String path,
                        String desciption,
                        String hivetableName,
                        String sortBy,
                        String sortType,
                        String datasetId) throws IOException{
        dataSetOptRepository.update(en_datasetName,ch_datasetName,path,desciption,hivetableName,sortBy,sortType,datasetId);
    }


    @Override
    public void clearByDataSetId (String datasetId) throws IOException{

    }

    @Override
    public void deleteByDataSetId (String datasetId) throws IOException{
        dataSetOptRepository.deleteByDataSetId(datasetId);
    }

    @Override
    public void upload (String datasetId,List<DataSetFile> fileList) throws IOException{
        dataSetOptRepository.upload(datasetId,fileList);
    }

    @Override
    public void sort (String datasetId,String sort) throws IOException{
        dataSetOptRepository.sort(datasetId,sort);
    }

}
