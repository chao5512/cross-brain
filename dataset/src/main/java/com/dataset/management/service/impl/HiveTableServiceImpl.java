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

/**
 * @ClassName HiveTableServiceImpl
 * @Description hive实体类
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
@Service
public class HiveTableServiceImpl implements HiveTableService {
    @Autowired
    private HiveRepository hiveRepository;
    /**
     * 功能描述:创建表
     * @param tableMeta
     * @param user
     * @param dataSet
     * @return: boolean
     * @auther: 王培文
     * @date: 2018/6/5 16:02
     */
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

    @Override
    @Transactional
    public boolean alterTableStructure(HiveTableMeta tableMeta, DataSet dataSet) {
        return hiveRepository.alterTableStructure(tableMeta,dataSet);
    }
}
