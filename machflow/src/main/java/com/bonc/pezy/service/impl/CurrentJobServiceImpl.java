package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.CurrentJobRepository;
import com.bonc.pezy.entity.CurrentJob;
import com.bonc.pezy.service.CurrentJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 冯刚 on 2018/7/30.
 */
@Service
public class CurrentJobServiceImpl implements CurrentJobService{

    @Autowired
    CurrentJobRepository currentJobRepository;

    @Override
    public CurrentJob save(CurrentJob currentJob) {
        return currentJobRepository.save(currentJob);
    }

    @Override
    public CurrentJob findByCJobId(String cJobId) {
        return currentJobRepository.findBycJobId(cJobId);
    }

    @Override
    public CurrentJob findByModelId(String modelid) {
        return currentJobRepository.findByModelId(modelid);
    }
}
