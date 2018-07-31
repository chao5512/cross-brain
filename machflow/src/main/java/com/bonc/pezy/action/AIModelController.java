package com.bonc.pezy.action;

import com.bonc.pezy.entity.Model;
import com.bonc.pezy.service.ModelService;
import com.bonc.pezy.util.ResultUtil;
import com.bonc.pezy.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("model")
@Api(value = "AI模型管理",description = "AI模型管理API,参数modelType:1-机器学习,2-深度学习,日期格式yyyyMMdd")
public class AIModelController {
    private final Logger logger = LoggerFactory.getLogger(AIModelController.class);

    @Autowired
    private ModelService modelService;

    @ApiOperation(value = "创建模型",httpMethod = "POST")
    @RequestMapping(value= "/create",method = RequestMethod.POST)
    @ResponseBody
    public Result createModel(@RequestParam(name="modelname") String modelName,
                       @RequestParam(name="modeltype") Short modelType,
                       @RequestParam(name="owner") Long owner, HttpServletResponse response){
        Result result = null;
        Model model = new Model();
        model.setModelName(modelName);
        model.setModelType(modelType);
        model.setOwner(owner);
        modelService.create(model);
        result = ResultUtil.success();
        return result;
    }

    @ApiOperation(value = "按用户ID查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByUser",method = RequestMethod.POST)
    @ResponseBody
    public Result findModelByUser(@RequestParam(name="owner") String owner,
                                        HttpServletResponse response){
        Result result = null;
        List<Model> models = modelService.findByUser(owner);
        result = ResultUtil.success(models);
        return result;
    }

    @ApiOperation(value = "按模型ID查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByModelId",method = RequestMethod.POST)
    @ResponseBody
    public Result findModelById(@RequestParam(name="modelId") String modelId,
                                HttpServletResponse response){
        Result result = null;
        Model model = modelService.findById(modelId);
        result = ResultUtil.success(model);
        return result;

    }

    @ApiOperation(value = "删除模型",httpMethod = "POST")
    @RequestMapping(value= "/delByModelId",method = RequestMethod.POST)
    @ResponseBody
    public Result delModel( @RequestParam(name="modelId[]",value="modelId[]") String[] modelId,
                           @RequestParam(name="owner") String owner,
                           HttpServletResponse response){
        Result result = null;
        long batch = modelService.delModel(modelId,owner);
        result = ResultUtil.success(batch);
        return result;
    }

    @ApiOperation(value = "按日期和类型查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByTypeAndCreateTime",method = RequestMethod.POST)
    @ResponseBody
    public Result findByTypeAndCreateTime(@RequestParam(name="startDate")String startDate,
                                               @RequestParam(name="endDate")String endDate,
                                               @RequestParam(name="modelType")short modelType,
                                               @RequestParam(name="owner") String owner){
        Result result = null;
        List<Model> models = modelService.findByCreateTimeAndType(startDate,endDate,modelType,owner);
        result = ResultUtil.success(models);
        return result;
    }

    @ApiOperation(value = "按模型名称模糊搜索模型",httpMethod = "POST")
    @RequestMapping(value= "/findByModelNameLike",method = RequestMethod.POST)
    @ResponseBody
    public Result findByModelNameLike(@RequestParam(name="modelName")String modelName,
                                           @RequestParam(name="owner") String owner){
        Result result = null;
        List<Model> models = modelService.findByModelNameLike(modelName,owner);
        result = ResultUtil.success(models);
        return result;
    }
}
