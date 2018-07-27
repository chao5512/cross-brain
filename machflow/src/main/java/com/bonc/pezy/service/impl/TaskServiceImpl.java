package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.TaskRepository;
import com.bonc.pezy.entity.Task;
import com.bonc.pezy.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 冯刚 on 2018/7/23.
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public List<Task> save(List<Task> tasks) {
        return taskRepository.save(tasks);
    }

    @Override
    public List<Task> findByJobId(String jobId) {
        return taskRepository.findTasksByJobId(jobId);
    }
}
