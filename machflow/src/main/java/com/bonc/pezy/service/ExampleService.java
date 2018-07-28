package com.bonc.pezy.service;

import com.bonc.pezy.entity.Example;

import java.util.List;

public interface ExampleService {
    public List<Example> findByModelNameContaining(String exampleName);
    public List<Example> findByExampleType(short exampleType);
    public List<Example> findAll();
}
