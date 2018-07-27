package com.bonc.pezy.service;

import com.bonc.pezy.entity.Job;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/23.
 */
public interface JobService {

    public Job create(Job job);

    public Job save(Job job);

    public Job findByJobId(String jobId);

    public List<Job> findByModelId(String modelid);
}
