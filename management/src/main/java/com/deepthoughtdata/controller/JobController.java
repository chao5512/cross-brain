package com.deepthoughtdata.controller;

import com.deepthoughtdata.entity.Job;
import com.deepthoughtdata.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("job")
@Api(value = "任务管理",description = "应用模块任务管理")
public class JobController {
    private final Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobService jobService;

    @ApiOperation(value = "任务查询",httpMethod = "POST")
    @RequestMapping(value= "/findByUser")
    @ResponseBody
    public List<Job> findModule( @RequestParam("owner") String owner,HttpServletResponse response){
        return jobService.findByUser(owner);
    }

}
