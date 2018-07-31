package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.CurrentNodeRepository;
import com.bonc.pezy.entity.CurrentNode;
import com.bonc.pezy.service.CurrentNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 冯刚 on 2018/7/30.
 */
@Service
public class CurrentNodeServiceImpl implements CurrentNodeService {

    @Autowired
    CurrentNodeRepository currentNodeRepository;

    @Override
    public CurrentNode save(CurrentNode currentNode) {
        return currentNodeRepository.save(currentNode);
    }
}
