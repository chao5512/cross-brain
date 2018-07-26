package com.bonc.pezy.action;

import com.bonc.pezy.entity.Model;
import com.bonc.pezy.service.ModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("model")
@Api(value = "AI模型管理",description = "AI模型管理API")
public class AIModelController {
    private final Logger logger = LoggerFactory.getLogger(AIModelController.class);

    @Autowired
    private ModelService modelService;

    @ApiOperation(value = "创建模型",httpMethod = "POST")
    @RequestMapping(value= "/create",method = RequestMethod.POST)
    @ResponseBody
    public boolean create(@ApiParam(name="modulename",value = "模型名称",required = true) String modulename,
                       @ApiParam(name="moduletype",value = "模型类型编码,1-机器学习,2-深度学习",required = true) short moduletype,
                       @ApiParam(name="owner",value = "用户ID",required = true) long owner, HttpServletResponse response){
        Model model = new Model();
        model.setModelName(modulename);
        model.setModelType(moduletype);
        model.setOwner(owner);
        model.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        model.setLastModifyTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        modelService.create(model);
        return true;
    }

    @ApiOperation(value = "按用户ID查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByUser",method = RequestMethod.POST)
    @ResponseBody
    public List<Model> findModuleByUser(@ApiParam(name="owner",value = "用户ID",required = true) String owner,
                                        HttpServletResponse response){
        return modelService.findByUser(owner);
    }

    @ApiOperation(value = "按模型ID查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByModelId",method = RequestMethod.POST)
    @ResponseBody
    public Model findModuleById(@ApiParam(name="modelid",value = "模型ID",required = true) String modelid,
                                HttpServletResponse response){
        return modelService.findById(modelid);
    }

    @ApiOperation(value = "按条件查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findModels",method = RequestMethod.POST)
    @ResponseBody
    public List<Model> findModules(@ApiParam(name="startData",value = "开始日期,格式yyyyMMdd",required = true) String startData,
                                   @ApiParam(name="endData",value = "结束日期,格式yyyyMMdd",required = true) String endData,
                                   @ApiParam(name="type",value = "模型类型编码,1-机器学习,2-深度学习",required = true) String type,
                                   @ApiParam(name="owner",value = "用户ID",required = true) String owner,
                                   HttpServletResponse response){
        return modelService.findModels(startData,endData,type,owner);
    }

    @ApiOperation(value = "删除模型",httpMethod = "POST")
    @RequestMapping(value= "/delByModelId",method = RequestMethod.POST)
    @ResponseBody
    public long delModule( @ApiParam(name="id[]",value = "模型ID",required = true) String[] id,
                           @ApiParam(name="owner",value = "用户ID",required = true) String owner,
                           HttpServletResponse response){
        return modelService.delModule(id,owner);
    }

    @ApiOperation(value = "按日期和类型查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByTypeAndCreateTime",method = RequestMethod.POST)
    @ResponseBody
    public List<Model> findByTypeAndCreateTime(@ApiParam(name="startData",value = "开始日期",required = true)String startData,
                                               @ApiParam(name="endData",value = "结束日期",required = true)String endData,
                                               @ApiParam(name="modelType",value = "模型类型编码",required = true)short modelType){

        return modelService.findByCreateTimeAndType(startData,endData,modelType);
    }

    @ApiOperation(value = "按模型名称模糊搜索模型",httpMethod = "POST")
    @RequestMapping(value= "/findByModelNameLike",method = RequestMethod.POST)
    @ResponseBody
    public List<Model> findByModelNameLike(@ApiParam(name="modelName",value="模糊查询关键词",required=true)String modelName){
        return  modelService.findByModelNameLike(modelName);
    }
}
