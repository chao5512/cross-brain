package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 冯刚 on 2018/7/6.
 */
public interface NodeDataRepository extends JpaRepository<Node,String> {

    Node save(Node node);

}
