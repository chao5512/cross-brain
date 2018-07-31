package com.bonc.pezy.service;

import com.bonc.pezy.entity.CurrentNode;
import org.springframework.stereotype.Service;

/**
 * Created by 冯刚 on 2018/7/30.
 */
@Service
public interface CurrentNodeService {

    public CurrentNode save(CurrentNode currentNode);
}
