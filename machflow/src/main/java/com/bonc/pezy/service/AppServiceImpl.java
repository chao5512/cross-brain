package com.bonc.pezy.service;

import com.bonc.pezy.dao.AppDataRepository;
import com.bonc.pezy.entity.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 冯刚 on 2018/7/6.
 */
@Service
public class AppServiceImpl implements AppService {
    private final Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);

    @Autowired
    private AppDataRepository appDataRepository;

    @Override
    public App save(App app) {
        app.setLastModifyTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        return appDataRepository.save(app);
    }

    @Override
    public App findById(String appid) {
        return null;
    }

    @Override
    public List<App> findByUser(String userid) {
        return appDataRepository.findByOwner(Long.parseLong(userid));
    }

    @Override
    public App findByUserAndAppId(String userid, long appid) {
        return appDataRepository.findByOwnerAndAppId(Long.parseLong(userid),appid);
    }

    @Override
    public App findByProcessId(String processId) {
        return appDataRepository.findByProcessId(processId);
    }

    @Override
    public App findByProcessId(String processId, long appid) {
        return appDataRepository.findByprocessIdAndAppId(processId,appid);
    }


}
