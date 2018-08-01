package com.bonc.pezy.service;

import com.bonc.pezy.entity.Node;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Created by 冯刚 on 2018/7/10.
 */
@Service
public interface NodeService {
    Node save(Node node);
    void deleteById(Long id);
    List<Node> findAll();
    Node findByClassName(String className);
}
