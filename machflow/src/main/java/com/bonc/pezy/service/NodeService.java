package com.bonc.pezy.service;

import com.bonc.pezy.entity.Node;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/10.
 */
@Service
public interface NodeService {

    public Node save(Node node);

    public List<Node> save(List<Node> nodes);


    public Node findById(String nodeid);

    public List<Node> findByAppId(int appId);

}
