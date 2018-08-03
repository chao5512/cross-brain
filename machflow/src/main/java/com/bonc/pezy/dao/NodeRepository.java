package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Node;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 冯刚 on 2018/7/6.
 */
public interface NodeRepository extends JpaRepository<Node,Long> {
    @Override
    Node save(Node node);

    @Override
    void deleteById(Long id);

    @Override
    List<Node> findAll();

    Node findByClassName(String className);
}
