package com.bonc.pezy.service;

import com.bonc.pezy.entity.Model;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ModelService {
    public Model create(Model module);

    public Model findById(String modelid);

    public List<Model> findByUser(String userid);

    public long delModel(String[] id, String userid);

    public List<Model> findByTimeAndType(String startData, String endData, short modelType, String owner);

    public List<Model> findByModelNameLike(String modelName,String owner);
}
