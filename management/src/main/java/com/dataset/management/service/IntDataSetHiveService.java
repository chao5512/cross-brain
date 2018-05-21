package com.dataset.management.service;

import com.dataset.management.entity.Hiveinfo;
import org.springframework.data.domain.Sort;

import java.util.List;


public interface IntDataSetHiveService {

    public Hiveinfo save(Hiveinfo hiveinfo);

    public List<Hiveinfo> findAll(Sort sort);

    public void deleteById(String id);

    public void updateById()
}
