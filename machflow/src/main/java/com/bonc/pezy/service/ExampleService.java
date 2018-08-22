package com.bonc.pezy.service;

import com.bonc.pezy.entity.Example;
import com.bonc.pezy.entity.Model;

import java.util.List;

public interface ExampleService {
    public List<Example> findByModelNameContaining(String exampleName);
    public List<Example> findByExampleType(short exampleType);
    public List<Example> findAll();
    public Model createModel(String exampleId,Long owner);
}
