package com.bonc.pezy.service;

import com.bonc.pezy.entity.CurrentJob;
import org.springframework.stereotype.Service;

/**
 * Created by 冯刚 on 2018/7/30.
 */
@Service
public interface CurrentJobService {

    public CurrentJob save(CurrentJob currentJob);

    public CurrentJob findByCJobId(String cJobId);

    public CurrentJob findByModelId(String modelid);
}
