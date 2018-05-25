package com.dataset.management.service;

import com.dataset.management.Dao.DataSetColumnRepository;
import com.dataset.management.entity.DataSetColumn;
import com.dataset.management.entity.Hiveinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DataSetColumService implements IntDataSetColumnService {

    @Autowired
    DataSetColumnRepository dataSetColumnRepository;

    public DataSetColumn save(DataSetColumn hiveinfo){
        return dataSetColumnRepository.save(hiveinfo);
    }

    public List<DataSetColumn> findAll(Sort sort){
        return dataSetColumnRepository.findAll(sort);
    }

    public DataSetColumn findByclnId(String id){
        return dataSetColumnRepository.findByclnId(id);
    }

    public void deleteById(String id){
        dataSetColumnRepository.deleteById(id);
    }

    public void updateById(String columnName,String clumnDesc,String id){
        dataSetColumnRepository.updateById(columnName,clumnDesc,id);
    }
}
