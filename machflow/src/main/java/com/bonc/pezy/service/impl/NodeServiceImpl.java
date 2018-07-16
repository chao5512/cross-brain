package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.NodeDataRepository;
import com.bonc.pezy.entity.Node;
import com.bonc.pezy.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/10.
 */
@Service
public class NodeServiceImpl implements NodeService{

    @Autowired
    private NodeDataRepository nodeDataRepository;

    @Override
    public Node save(Node node) {

        return nodeDataRepository.save(node);
    }

    @Override
    public List<Node> save(List<Node> nodes) {
        return nodeDataRepository.save(nodes);
    }

    @Override
    public Node findById(String nodeid) {
        return null;
    }

    @Override
    public List<Node> findByAppId(int appId) {
        return nodeDataRepository.findNodesByAppId(appId);
    }
}
