package com.dataset.management.service.impl;

import com.dataset.management.dao.hivedao.HiveRepository;
import com.dataset.management.dao.hivedao.impl.HiveRepositoryImpl;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;
import com.dataset.management.service.HiveTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * @ClassName HiveTableServiceImpl
 * @Description hive实体类
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
@Service
public class HiveTableServiceImpl implements HiveTableService {

    private static Logger logger = LoggerFactory.getLogger(HiveTableServiceImpl.class);
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
    public boolean createTable(HiveTableMeta tableMeta, User user, DataSet dataSet) throws IOException {
        boolean exist = hiveRepository.isExist(dataSet);
        if(!exist){
            hiveRepository.createTable(tableMeta,user,dataSet);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 功能描述:更改表结构
     * @param tableMeta
     * @param dataSet
     * @return: boolean
     * @auther: 王培文
     * @date: 2018/6/8 9:23
     */
    @Override
    @Transactional
    public boolean alterTableStructure(HiveTableMeta tableMeta, User user,DataSet dataSet) {
        try {
            String tableName = tableMeta.getTableName();
            //DROP TABLE IF EXISTS employee
            boolean isDelete = hiveRepository.dropTableByName(tableName);
            if(isDelete){
                boolean isCreate = hiveRepository.createTable(tableMeta, user, dataSet);
                boolean result = isCreate ? true:false;
                return result;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.valueOf(e.getStackTrace()));
            return false;
        }
    }

    /**
     * 功能描述:判断表是否存在
     * @param dataSet
     * @return: boolean
     * @auther: 王培文
     * @date: 2018/6/8 9:23
     */
    @Override
    public boolean isExist(DataSet dataSet) {
        return hiveRepository.isExist(dataSet);
    }

    /**
     * 功能描述:根据数据集查表明
     * @param dataSet
     * @return: java.lang.String
     * @auther: 王培文
     * @date: 2018/6/22 10:36
     */
    @Override
    public String getTableNameByDataSet(DataSet dataSet) {
        return hiveRepository.getTableNameByDataSet(dataSet);
    }
}
