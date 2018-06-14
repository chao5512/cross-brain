package com.deepthoughtdata.dao;

import com.deepthoughtdata.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByOwner(long owner);//根据用户ID查询Module
}
