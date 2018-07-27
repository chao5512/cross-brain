package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/23.
 */
public interface TaskRepository extends JpaRepository<Task,String> {

    Task save(Task task);

    List<Task> save(List<Task> tasks);


    List<Task> findTasksByJobId(String  jobId);

}
