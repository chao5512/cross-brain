package com.bonc.pezy.action;

import com.bonc.pezy.flow.MachFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by 冯刚 on 2018/6/14.
 */

@Controller
@RequestMapping("/machflow")
public class MachFlowController {


    private final Logger logger = LoggerFactory.getLogger(MachFlowController.class);

    @RequestMapping("/analysisCanvas")
    @ResponseBody
    public String analysisCanvas(@RequestParam("jsondata") String jsondata,HttpServletResponse response){

        MachFlow mf = new MachFlow();
        mf.generateBpmnModel(jsondata);
        return "sucess";
    }

    @RequestMapping("/run")
    @ResponseBody
    public void run(@RequestParam("processId") String processId,HttpServletResponse respons){
        MachFlow mf = new MachFlow();
        mf.startActiviti(processId);
    }
}
