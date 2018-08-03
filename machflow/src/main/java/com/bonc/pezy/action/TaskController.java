package com.bonc.pezy.action;

import com.bonc.pezy.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by 冯刚 on 2018/8/3.
 */
@Controller
@RequestMapping("task")
@Api(value = "任务节点信息", description = "任务节点的管理")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @ApiOperation(value = "更新任务节点状态", httpMethod = "GET")
    @RequestMapping(value = "/updateTask", method = RequestMethod.GET)
    @ResponseBody
    public void updateTask(@RequestParam("jobId") String jobId,
                           @RequestParam("taskId") String taskId,
                           @RequestParam("status") short status,HttpServletResponse respons){
        taskService.updateByJobIdAndTaskId(status,jobId,taskId);
    }
}
