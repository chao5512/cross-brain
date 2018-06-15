package com.bonc.pezy.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by 冯刚 on 2018/6/15.
 */

@Controller
@RequestMapping("work")
public class WorkFlowController {

    private final Logger logger = LoggerFactory.getLogger(MachFlowController.class);

    @RequestMapping("/analysisCanvas")
    @ResponseBody
    public String analysisCanvas(@RequestParam("json") String json, HttpServletResponse response){

        System.out.println("ddddddd");
/*
        Map jb = JSONObject.parseObject(json);

        MachFlow mf = new MachFlow();
        mf.generateBpmnModel(jb);

        mf.startActiviti();*/

        return "ssss";

    }


    @RequestMapping(value="/hello")
    public void hello(){
        System.out.print("hello word!");
    }
}
