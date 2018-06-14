package com.deepthoughtdata.service;

import com.deepthoughtdata.entity.Job;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface JobService {
    public List<Job> findByUser(String userid);
}
