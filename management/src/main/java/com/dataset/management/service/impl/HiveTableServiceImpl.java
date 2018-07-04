package com.dataset.management.service.impl;

import com.dataset.management.Dao.HiveRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;
import com.dataset.management.service.HiveTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.crypto.Data;

@Service
public class HiveTableServiceImpl implements HiveTableService {
    @Autowired
    private HiveRepository hiveRepository;
    @Override
    @Transactional
    public boolean createTable(HiveTableMeta tableMeta, User user, DataSet dataSet) {
        boolean exist = hiveRepository.isExist(dataSet);
        if(!exist){
            hiveRepository.createTable(tableMeta,user,dataSet);
            return true;
        }else {
            return false;
        }
    }
}
