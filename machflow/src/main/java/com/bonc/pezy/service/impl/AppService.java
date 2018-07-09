package com.bonc.pezy.service.impl;

import com.bonc.pezy.entity.App;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/6.
 */
@Service
public interface AppService {

    public App save(App module);

    public App findById(String modelid);

    public List<App> findByUser(String userid);

    public App findByUserAndAppId(String userid,int appid);
}
