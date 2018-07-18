package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/6.
 */
public interface NodeDataRepository extends JpaRepository<Node,String> {

    Node save(Node node);

    List<Node> save(List<Node> nodes);

    List<Node> findNodesByAppId(long  appId);

    void deleteByAppId(long  appId);

}
