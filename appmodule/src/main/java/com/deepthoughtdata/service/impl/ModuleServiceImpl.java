package com.deepthoughtdata.service.impl;

import com.deepthoughtdata.dao.ModuleRepository;
import com.deepthoughtdata.service.ModuleService;
import com.deepthoughtdata.entity.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.transaction.Transactional;
import java.util.Date;

@Service
public class ModuleServiceImpl implements ModuleService{
    private final Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public Model create(Model module){
        module.setLastModifyTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        return moduleRepository.save(module);
    }

    @Override
    public List<Model> findByUser(String userid){
        return moduleRepository.findByOwner(Long.parseLong(userid));
    }

    @Override
    public List<Model> findModels(String startData, String endData, String type, String userid){
        return moduleRepository.findByModelTypeAndOwnerAndCreateTimeAfterAndCreateTimeBefore(Integer.parseInt(type),
                Long.parseLong(userid),startData,endData);
    }

    @Override
    @Transactional
    public long delModule(String id,String userid){
        return moduleRepository.deleteByModelIdAndOwner(Long.parseLong(id),Long.parseLong(userid));
    }

    @Override
    public Model findById(long modelid){
        return moduleRepository.findByModelId(modelid);
    }

}
