package com.bonc.pezy.service;

import com.bonc.pezy.entity.Task;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/23.
 */
public interface TaskService {

    public Task save(Task task);
    public List<Task> save(List<Task> tasks);

    public List<Task> findByJobId(String jobId);

}
