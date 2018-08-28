package com.bonc.pezy.algorithmmodel.classification;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.dataconfig.ServiceMap;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.entity.Task;
import com.bonc.pezy.pyapi.JavaRequestPythonService;
import com.bonc.pezy.service.JobService;
import com.bonc.pezy.service.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 冯刚 on 2018/6/13.
 */
public class LRExectuionListener implements Serializable, ExecutionListener{

    private static final long serialVersionUID = 8513750196548027535L;

    private ServiceMap serviceMap = ServiceMap.getServiceMap();

    private JobService jobService = serviceMap.getJobService();
    private TaskService taskService = serviceMap.getTaskService();

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String eventName = execution.getEventName();
        if ("start".equals(eventName)) {
            Job job = jobService.findByJobId(execution.getBusinessKey());
            String url = null;
            Map<String,Object> param = new LinkedHashMap();
            Map<String,Object> map = new LinkedHashMap<>();
            String pipe = null;
            List<Task> tasks = taskService.findByJobId(job.getJobId());
            param.put("appName",job.getJobName());
            param.put("jobId",job.getJobId());
            param.put("modelId",job.getModelId());
            param.put("userId", String.valueOf(job.getOwner()));
            if(job.getModelType() == 1){
                url = Constants.PY_SERVER;

            }
            if(job.getModelType()==2){
                url = Constants.PY_SERVER_DEEP;
            }
            for(Task task:tasks){
                Map<String,Object> tmp = new HashMap();
                tmp.put("taskId",task.getTaskId());
                tmp.put("type",task.getTaskType());
                System.out.println(JSON.parse(task.getParam()));
                param.put(task.getTaskName(),JSON.parse(task.getParam()));
                map.put(task.getTaskName(),tmp);
            }
            param.put("tasks",map);
            pipe = JSONUtils.toJSONString(param);
            System.out.println("请求JSON："+pipe);
            if (!"".equals(pipe)){
                JavaRequestPythonService jrps = new JavaRequestPythonService();
                String result = jrps.requestPythonService(pipe,url);
                JSONObject resultjson = JSON.parseObject(result);
                String applicationid = resultjson.get("applicationId").toString();
                int status = Integer.parseInt(resultjson.get("status").toString());
                String message = resultjson.get("msg").toString();
                execution.setVariable("msg",message);
                job.setJobStatus(status);
                job.setApplicationId(applicationid);
                jobService.save(job);
            }
        }else if ("end".equals(eventName)) {
            System.out.println("end=========");
            System.out.println("===xxxx===="+execution.getEventName());

        }

    }
}
