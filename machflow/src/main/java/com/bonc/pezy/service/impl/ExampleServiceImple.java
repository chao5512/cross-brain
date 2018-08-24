package com.bonc.pezy.service.impl;

import com.bonc.pezy.dao.ExampleRepository;
import com.bonc.pezy.dao.JobRepository;
import com.bonc.pezy.dao.ModelRepository;
import com.bonc.pezy.dao.TaskRepository;
import com.bonc.pezy.entity.Example;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.entity.Model;
import com.bonc.pezy.entity.Task;
import com.bonc.pezy.service.ExampleService;
import com.bonc.pezy.service.ModelService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("exampleService")
public class ExampleServiceImple implements ExampleService{
    @Autowired
    private ExampleRepository exampleRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<Example> findByModelNameContaining(String exampleName) {
        return exampleRepository.findByExampleNameContaining(exampleName);
    }

    @Override
    public List<Example> findByExampleType(short exampleType) {
        return exampleRepository.findByExampleType(exampleType);
    }

    @Override
    public List<Example> findAll() {
        return exampleRepository.findAll();
    }

    @Override
    public Model createModel(String exampleId,Long owner,String modelName) {
        //根据exampleId查modelId
        Example example = exampleRepository.findByExampleId(exampleId);

        //查Model
        Model model = modelRepository.findByModelId(example.getModelId());
        String modelId = model.getModelId();
        Model modelTemp = new Model();
        modelTemp.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        modelTemp.setLastModifyTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        modelTemp.setModelName(model.getModelName());
        modelTemp.setModelType(model.getModelType());
        modelTemp.setOwner(owner);
        modelTemp.setModelName(modelName);
        modelTemp = modelRepository.save(modelTemp);

        //根据ModelId查询job
        List<Job> jobs = jobRepository.findByModelId(modelId);
        for (Job job : jobs) {
            String jobId = job.getJobId();
            Job jobTemp = new Job();
            BeanUtils.copyProperties(job, jobTemp);
            jobTemp.setModelId(modelTemp.getModelId());
            jobTemp.setJobId(null);
            jobTemp.setTasks(null);
            jobTemp.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//            jobTemp.setStopTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

            jobRepository.save(jobTemp);
            //根据jobId查询task 复制
            List<Task> tasks = taskRepository.findTasksByJobId(jobId);
            for (Task task : tasks) {
                Task temp = new Task();
                BeanUtils.copyProperties(task, temp);
                temp.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                temp.setTaskId(null);
                temp.setJobId(jobTemp.getJobId());
                taskRepository.save(temp);
            }

        }

        return modelTemp;
    }
}
