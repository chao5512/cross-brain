package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.ExampleRepository;
import com.bonc.pezy.entity.Example;
import com.bonc.pezy.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("exampleService")
public class ExampleServiceImple implements ExampleService{
    @Autowired
    private ExampleRepository exampleRepository;

    @Override
    public List<Example> findByModelNameContaining(String exampleName) {
        return exampleRepository.findByExampleNameContaining(exampleName);
    }

    @Override
    public List<Example> findByExampleType(short exampleType) {
        return exampleRepository.findByExampleType(exampleType);
    }

    @Override
    public List<Example> findAll() {
        return exampleRepository.findAll();
    }
}
