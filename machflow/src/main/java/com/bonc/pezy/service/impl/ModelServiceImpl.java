package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.ModelRepository;
import com.bonc.pezy.service.ModelService;
import com.bonc.pezy.entity.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import javax.transaction.Transactional;
import java.util.Date;

@Service
public class ModelServiceImpl implements ModelService {
    private final Logger logger = LoggerFactory.getLogger(ModelServiceImpl.class);

    @Autowired
    private ModelRepository moduleRepository;

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
        return moduleRepository.findByModelTypeAndOwnerAndCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(Short.parseShort(type),
                Long.parseLong(userid),startData,endData);
    }

    @Override
    @Transactional
    public long delModule(String[] id,String userid){
        return moduleRepository.deleteByIds(Arrays.asList(id),Long.parseLong(userid));
    }

    @Override
    public Model findById(String modelid){
        return moduleRepository.findByModelId(modelid);
    }

}
