package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExampleRepository extends JpaRepository<Example, Long> {
    List<Example> findByExampleNameContaining(String exampleName);
    List<Example> findByExampleType(short exampleType);
    Example findByExampleId(String exampleId);
}
