package com.bonc.pezy.dataconfig;

import com.bonc.pezy.service.*;

/**
 * Created by 冯刚 on 2018/7/14.
 */
public class ServiceMap {

    private static ServiceMap serviceMap = null;

    private ServiceMap(){

    }

    public static ServiceMap getServiceMap(){

        if(null == serviceMap){
            serviceMap = new ServiceMap();
        }
        return serviceMap;
    }

    private AppService appService;

    private NodeService nodeService;

    private ModelService modelService;

    private JobService jobService;

    private TaskService taskService;

    public AppService getAppService() {
        return appService;
    }

    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public JobService getJobService() {
        return jobService;
    }

    public void setJobService(JobService jobService) {
        this.jobService = jobService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
}

