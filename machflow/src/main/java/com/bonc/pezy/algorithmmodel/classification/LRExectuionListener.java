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
            System.out.println("start=========");
            System.out.println("===xxxx===="+execution.getEventName()+"====yyyyy="+execution.getBusinessKey());
            Job job = jobService.findByJobId(execution.getBusinessKey());
            String url = null;
            Map<String,String> param = new HashMap<String, String>();
            String pipe = null;
            System.out.println("===xxxx===="+job.getModelType());
            List<Task> tasks = taskService.findByJobId(job.getJobId());

            if(job.getModelType() == 1){
                url = Constants.PY_SERVER;
                param.put("appName",job.getJobName());
                for(Task task:tasks){

                    param.put(task.getTaskName(),task.getParam());

                }

            }
            if(job.getModelType()==2){
                url = Constants.PY_SERVER_DEEP;
                param.put("appName",job.getJobName());
                for(Task task:tasks){

                    param.put(task.getTaskName(),task.getParam());

                }
            }

            pipe = JSONUtils.toJSONString(param);
            System.out.println(pipe);
            System.out.println(url);
            if (!"".equals(pipe)){
                JavaRequestPythonService jrps = new JavaRequestPythonService();
                String result = jrps.requestPythonService(pipe,url);
                JSONObject resultjson = JSON.parseObject(result);
                String applicationid = resultjson.get("applicationId").toString();
                int status = Integer.parseInt(resultjson.get("status").toString());
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
