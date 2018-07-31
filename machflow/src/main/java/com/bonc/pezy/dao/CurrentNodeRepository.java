package com.bonc.pezy.dao;

import com.bonc.pezy.entity.CurrentNode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 冯刚 on 2018/7/30.
 */
public interface CurrentNodeRepository extends JpaRepository<CurrentNode,String> {

    CurrentNode save(CurrentNode currentNode);

}
