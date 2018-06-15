package com.deepthoughtdata.service.impl;

import com.deepthoughtdata.dao.ModuleRepository;
import com.deepthoughtdata.service.ModuleService;
import com.deepthoughtdata.entity.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import javax.transaction.Transactional;

@Service
public class ModuleServiceImpl implements ModuleService{
    private final Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public Module create(Module module){
        return moduleRepository.save(module);
    }

    @Override
    public List<Module> findByUser(String userid){
        return moduleRepository.findByOwner(Long.parseLong(userid));
    }

    @Override
    @Transactional
    public long delModule(String id,String userid){
        return moduleRepository.deleteByIdAndOwner(Long.parseLong(id),Long.parseLong(userid));
    }
}
