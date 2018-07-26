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

import java.util.List;

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
    public boolean createModel(@RequestParam(name="modelname") String modelName,
                       @RequestParam(name="modeltype") Short modelType,
                       @RequestParam(name="owner") Long owner, HttpServletResponse response){
        Model model = new Model();
        model.setModelName(modelName);
        model.setModelType(modelType);
        model.setOwner(owner);
        modelService.create(model);
        return true;
    }

    @ApiOperation(value = "按用户ID查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByUser",method = RequestMethod.POST)
    @ResponseBody
    public List<Model> findModelByUser(@RequestParam(name="owner") String owner,
                                        HttpServletResponse response){
        return modelService.findByUser(owner);
    }

    @ApiOperation(value = "按模型ID查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByModelId",method = RequestMethod.POST)
    @ResponseBody
    public Model findModelById(@RequestParam(name="modelid") String modelid,
                                HttpServletResponse response){
        return modelService.findById(modelid);
    }

    //与下“/findByTypeAndCreateTime”功能相同
//    @ApiOperation(value = "按条件查询模型",httpMethod = "POST")
//    @RequestMapping(value= "/findModels",method = RequestMethod.POST)
//    @ResponseBody
//    public List<Model> findModels(@RequestParam(name="startData") String startData,
//                                   @RequestParam(name="endData") String endData,
//                                   @RequestParam(name="type") String type,
//                                   @RequestParam(name="owner") String owner,
//                                   HttpServletResponse response){
//        return modelService.findModels(startData,endData,type,owner);
//    }

    @ApiOperation(value = "删除模型",httpMethod = "POST")
    @RequestMapping(value= "/delByModelId",method = RequestMethod.POST)
    @ResponseBody
    public long delModel( @RequestParam(name="id[]") String[] id,
                           @RequestParam(name="owner") String owner,
                           HttpServletResponse response){
        return modelService.delModel(id,owner);
    }

    @ApiOperation(value = "按日期和类型查询模型",httpMethod = "POST")
    @RequestMapping(value= "/findByTypeAndCreateTime",method = RequestMethod.POST)
    @ResponseBody
    public List<Model> findByTypeAndCreateTime(@RequestParam(name="startData")String startData,
                                               @RequestParam(name="endData")String endData,
                                               @RequestParam(name="modelType")short modelType,
                                               @RequestParam(name="owner") String owner){

        return modelService.findByCreateTimeAndType(startData,endData,modelType,owner);
    }

    @ApiOperation(value = "按模型名称模糊搜索模型",httpMethod = "POST")
    @RequestMapping(value= "/findByModelNameLike",method = RequestMethod.POST)
    @ResponseBody
    public List<Model> findByModelNameLike(@RequestParam(name="modelName")String modelName,
                                           @RequestParam(name="owner") String owner){
        return  modelService.findByModelNameLike(modelName,owner);
    }
}
