package com.deepthoughtdata.dao;

import com.deepthoughtdata.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModelRepository extends JpaRepository<Model, Long> {
    Model save(Model module); //增加&修改用户

    List<Model> findByOwner(long owner);//根据用户ID查询Module

    long deleteByModelIdAndOwner(long id,long owner);//根据用户ID查询Module

    Model findByModelId(long id);

    List<Model> findByModelTypeAndOwnerAndCreateTimeAfterAndCreateTimeBefore(int type, long owner, String startData, String endData);//根据用户ID查询Module
}
