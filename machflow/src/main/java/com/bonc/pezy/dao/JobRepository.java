package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Job;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 冯刚 on 2018/7/23.
 */
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {

    Job save(Job job);

    Job findByJobId(String jobId);

    List<Job> findByModelId(String modelid);
}
