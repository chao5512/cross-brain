package com.dataset.management.service;

import com.dataset.management.PrimaryDao.DataSetOptRepository;
import com.dataset.management.entity.DataSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public DataSystem findById(int Id)throws IOException{
        return dataSetOptRepository.findById(Id);
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
    public List<DataSystem> findByUserId(int userId){
        return dataSetOptRepository.findByUserId(userId);
    }

    @Override
    public List<DataSystem> findAll(Sort sort)throws IOException{
        return dataSetOptRepository.findAll(sort);
    }

    public void deleteAll(){
        dataSetOptRepository.deleteAll();
    }

    @Override
    @Transactional
    public void deleteById (int Id) throws IOException{
        dataSetOptRepository.deleteById(Id);
    }

}
