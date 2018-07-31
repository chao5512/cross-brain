package com.bonc.pezy.action;

import com.bonc.pezy.service.ExampleService;
import com.bonc.pezy.util.ResultUtil;
import com.bonc.pezy.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("example")
@Api(value = "AI案例管理",description = "AI模型管理API,参数exampleType:1-分类算法,2-预测算法")

public class AIExampleController {
    @Autowired
    @Qualifier("exampleService")
    private ExampleService exampleService;

    @ApiOperation(value = "查询全部案例",httpMethod = "POST")
    @RequestMapping(value= "/findAll",method = RequestMethod.POST)
    @ResponseBody
    public Result findAll(){
        return ResultUtil.success(exampleService.findAll());
    }

    @ApiOperation(value = "按名称模糊查询案例",httpMethod = "POST")
    @RequestMapping(value= "/findByExampleNameContaining",method = RequestMethod.POST)
    @ResponseBody
    public Result findByExampleNameContaining(@RequestParam(name="exampleName")String exampleName){
        return ResultUtil.success(exampleService.findByModelNameContaining(exampleName));
    }

    @ApiOperation(value = "按类型查询案例",httpMethod = "POST")
    @RequestMapping(value= "/findByExampleType",method = RequestMethod.POST)
    @ResponseBody
    public Result findByExampleType(@RequestParam(name="exampleType")short exampleType){
        return ResultUtil.success(exampleService.findByExampleType(exampleType));
    }

    }
