package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Job;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/23.
 */
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {

    Job save(Job job);

    Job findByJobId(String jobId);

    @Transactional
    List<Job> findByModelId(String modelid, Sort sort);

    @Transactional
    @Modifying
    @Query("update job as j set j.status = ?1 where j.jobid=?2")
    int updateStatusByJobId(short status, String jobid);




}
