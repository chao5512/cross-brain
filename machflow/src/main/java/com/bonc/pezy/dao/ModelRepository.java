package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface ModelRepository extends JpaRepository<Model, Long> {
    Model save(Model model); //增加&修改用户

    List<Model> findByOwner(long owner);//根据用户ID查询Module

    @Modifying
    @Transactional
    @Query(value="delete from Model where modelid in (:ids) and owner = :owner ") int deleteByIds(@Param("ids") List<String> ids, @Param("owner") long owner);

    Model findByModelId(String modelId);

    List<Model> findByModelTypeAndOwnerAndCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(short type, long owner, String startData, String endData);//根据用户ID查询Module

    List<Model> findByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqualAndModelType(String startData, String endData,short modelType);

    List<Model> findByModelNameContaining(String modelName);

}
