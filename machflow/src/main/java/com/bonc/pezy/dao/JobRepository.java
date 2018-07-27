package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/23.
 */
public interface JobRepository extends JpaRepository<Job,String> {

    Job save(Job job);

    Job findByJobId(String jobId);

    List<Job> findByModelId(String modelid);
}
