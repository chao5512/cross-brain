package com.dataset.management.Dao;

import com.dataset.management.entity.DataSetColumn;
import com.dataset.management.entity.Hiveinfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataSetColumnRepository extends JpaRepository<DataSetColumn,String > {

    public DataSetColumn save(DataSetColumn hiveinfo);

    public List<DataSetColumn> findAll(Sort sort);

    public DataSetColumn findByclnId(String id);

    public void deleteById(String id);

    public void updateById(String columnName,String clumnDesc,String id);
}
