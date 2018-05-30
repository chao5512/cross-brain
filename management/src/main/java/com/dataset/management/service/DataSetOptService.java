package com.dataset.management.service;

import com.dataset.management.Dao.DataSetOptRepository;
import com.dataset.management.entity.DataSystem;
import com.dataset.management.entity.DataSetFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DataSetOptService implements IntDataSetOptService {

    @Autowired
    private DataSetOptRepository dataSetOptRepository;

    @Override
    public DataSystem save(DataSystem dataSystem) throws IOException{
        return dataSetOptRepository.save(dataSystem);
    }

    @Override
    public DataSystem findById(int datasetId)throws IOException{
        return dataSetOptRepository.findById(datasetId);
    }

    @Override
    public DataSystem findByDataSetId(int datasetId) throws IOException{
        return dataSetOptRepository.findByDataSetId(datasetId);
    }

    @Override
    public DataSystem findByDataSetName(String datasetName)throws IOException{
        return dataSetOptRepository.findByDataSetName(datasetName);
    }
    @Override
    public List<DataSystem> findByUserName(String userName){
        return dataSetOptRepository.findByUserName(userName);
    }

    @Override
    public List<DataSystem> findAll(Sort sort)throws IOException{
        return dataSetOptRepository.findAll(sort);
    }

    public void deleteAll(){
        dataSetOptRepository.deleteAll();
    }

    @Override
    public void deleteById (int Id) throws IOException{
        dataSetOptRepository.deleteById(Id);
    }

}
