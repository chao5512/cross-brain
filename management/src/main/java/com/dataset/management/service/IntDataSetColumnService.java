package com.dataset.management.service;

import com.dataset.management.entity.DataSetColumn;
import com.dataset.management.entity.Hiveinfo;
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Sort;

import java.util.List;


public interface IntDataSetColumnService {

    public DataSetColumn save(DataSetColumn hiveinfo);

    public List<DataSetColumn> findAll(Sort sort);

    public DataSetColumn findByclnId(String id);

    public void deleteById(String id);

    public void updateById(String columnName,String clumnDesc,String id);
}
