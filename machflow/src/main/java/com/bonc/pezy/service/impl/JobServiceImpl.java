package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.JobRepository;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/23.
 */
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Override
    public Job create(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public Job save(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public Job findByJobId(String jobId) {
        return jobRepository.findByJobId(jobId);
    }

    @Override
    public List<Job> findByModelId(String modelid) {
        return jobRepository.findByModelId(modelid);
    }
}
