package com.deepthoughtdata.service.impl;

import com.deepthoughtdata.dao.JobRepository;
import com.deepthoughtdata.entity.Job;
import com.deepthoughtdata.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    private final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    private JobRepository jobRepository;

    @Override
    public List<Job> findByUser(String userid){
        return jobRepository.findByOwner(Long.parseLong(userid));
    }
}
