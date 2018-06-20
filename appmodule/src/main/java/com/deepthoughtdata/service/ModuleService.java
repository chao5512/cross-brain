package com.deepthoughtdata.service;

import com.deepthoughtdata.entity.Module;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ModuleService {
    public Module create(Module module);

    public List<Module> findByUser(String userid);

    public long delModule(String id,String userid);

    public List<Module> findModels(String startData,String endData,String type,String userid);
}
