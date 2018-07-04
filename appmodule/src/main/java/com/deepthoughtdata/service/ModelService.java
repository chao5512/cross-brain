package com.deepthoughtdata.service;

import com.deepthoughtdata.entity.Model;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ModelService {
    public Model create(Model module);

    public Model findById(String modelid);

    public List<Model> findByUser(String userid);

    public long delModule(String id,String userid);

    public List<Model> findModels(String startData, String endData, String type, String userid);
}
