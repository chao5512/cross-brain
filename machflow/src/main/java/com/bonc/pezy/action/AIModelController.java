package com.bonc.pezy.action;

import com.bonc.pezy.entity.Model;
import com.bonc.pezy.service.ModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @RequestMapping(value= "/create")
    @ResponseBody
    public boolean create(@RequestParam("modulename") String modulename,
                       @RequestParam("moduletype") short moduletype,
                       @RequestParam("owner") long owner,
                       @RequestParam("createtime") String createtime, HttpServletResponse response){
        Model model = new Model();
        model.setModelName(modulename);
        model.setModelType(moduletype);
        model.setOwner(owner);
        model.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        model.setLastModifyTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        modelService.create(model);
        return true;
    }

    @ApiOperation(value = "模型查询",httpMethod = "POST")
    @RequestMapping(value= "/findByUser")
    @ResponseBody
    public List<Model> findModuleByUser(@RequestParam("owner") String owner, HttpServletResponse response){
        return modelService.findByUser(owner);
    }

    @ApiOperation(value = "根据ID查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByModelId")
    @ResponseBody
    public Model findModuleById(@RequestParam("modelid") String modelid, HttpServletResponse response){
        return modelService.findById(modelid);
    }

    @ApiOperation(value = "模型查询",httpMethod = "POST")
    @RequestMapping(value= "/findModels")
    @ResponseBody
    public List<Model> findModules(@RequestParam("startData") String startData,
                                   @RequestParam("endData") String endData,
                                   @RequestParam("type") String type,
                                   @RequestParam("owner") String owner, HttpServletResponse response){
        return modelService.findModels(startData,endData,type,owner);
    }

    @ApiOperation(value = "删除模型",httpMethod = "POST")
    @RequestMapping(value= "/delByModelId")
    @ResponseBody
    public long delModule( @RequestParam(name="id[]") String[] id,@RequestParam("owner") String owner,HttpServletResponse response){
        return modelService.delModule(id,owner);
    }

    @ApiOperation(value = "按日期和类型查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByTypeAndCreateTime")
    @ResponseBody
    public List<Model> findByTypeAndCreateTime(@RequestParam(name="startData")String startData,@RequestParam(name="endData")String endData,@RequestParam(name="modelType")short modelType){

        return modelService.findByCreateTimeAndType(startData,endData,modelType);
    }

    @ApiOperation(value = "按模型名称模糊搜索模型",httpMethod = "POST")
    @RequestMapping(value= "/findByModelNameLike")
    @ResponseBody
    public List<Model> findByModelNameLike(@RequestParam(name="modelName")String modelName){
        return  modelService.findByModelNameLike(modelName);
    }
}
