package com.bonc.pezy.dao;

import com.bonc.pezy.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/5.
 */
public interface AppDataRepository extends JpaRepository<App,String> {

    App save(App app);

    List<App> findByOwner(long owner);//根据用户ID查询Module

    App findByOwnerAndAppId(long owner,int appid);

}
