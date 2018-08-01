package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.NodeRepository;
import com.bonc.pezy.entity.Node;
import com.bonc.pezy.service.NodeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 冯刚 on 2018/7/10.
 */
@Service
public class NodeServiceImpl implements NodeService{

    @Autowired
    private NodeRepository nodeRepository;

    @Override
    public Node save(Node node) {
        return nodeRepository.save(node);
    }

    @Override
    public void deleteById(Long id) {
        nodeRepository.deleteById(id);
    }

    @Override
    public List<Node> findAll() {
        return nodeRepository.findAll();
    }

    @Override
    public Node findByClassName(String className) {
        return nodeRepository.findByClassName(className);
    }


}
