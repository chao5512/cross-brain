package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.NodeDataRepository;
import com.bonc.pezy.entity.Node;
import com.bonc.pezy.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Node> findByAppId(long appId) {
        return nodeDataRepository.findNodesByAppId(appId);
    }

    @Modifying
    @Transactional
    @Query(value = "delete from Node n where n.appId=?1")
    @Override
    public void deleteByAppId(long appId) {
        nodeDataRepository.deleteByAppId(appId);
    }

}
