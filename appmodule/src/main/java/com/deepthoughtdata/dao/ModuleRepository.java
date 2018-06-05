package com.deepthoughtdata.dao;

import com.deepthoughtdata.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    Module save(Module module); //增加&修改用户

    List<Module> findByOwner(long owner);//根据用户ID查询Module

    long deleteByIdAndOwner(long id,long owner);//根据用户ID查询Module
}
