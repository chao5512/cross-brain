package com.bonc.pezy.dao;

import com.bonc.pezy.entity.Job;
import com.bonc.pezy.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/23.
 */
public interface TaskRepository extends JpaRepository<Task,String> {

    Task save(Task task);

    List<Task> save(List<Task> tasks);


    List<Task> findTasksByJobId(String  jobId);

    @Transactional
    @Modifying
    @Query("update Task as t set t.taskStatus=?1 where t.jobId=?2 and t.taskId=?3")
    Task updateByJobIdAndTaskId(int taskStatus, String jobId,String taskId);

}
