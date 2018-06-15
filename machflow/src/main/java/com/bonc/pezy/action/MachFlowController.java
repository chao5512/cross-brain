package com.bonc.pezy.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 冯刚 on 2018/6/14.
 */

@Controller
@RequestMapping("module")
@Api(value = "画布转化",description = "画布转化类")
public class MachFlowController {

    private final Logger logger = LoggerFactory.getLogger(MachFlowController.class);

    @ApiOperation(value = "解析画布",httpMethod = "POST")
    @RequestMapping(value= "/analysisCanvas")
    @ResponseBody
    public void analysisCanvas(){



    }
}
