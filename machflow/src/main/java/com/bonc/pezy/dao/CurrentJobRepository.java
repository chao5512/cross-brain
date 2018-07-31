package com.bonc.pezy.dao;

import com.bonc.pezy.entity.CurrentJob;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 冯刚 on 2018/7/30.
 */
public interface CurrentJobRepository extends JpaRepository<CurrentJob,String> {

    CurrentJob save(CurrentJob currentJob);

    CurrentJob findBycJobId(String cJobId);

    CurrentJob findByModelId(String modelid);
}
