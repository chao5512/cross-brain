package com.deepthoughtdata.controller;

import com.deepthoughtdata.entity.Module;
import com.deepthoughtdata.service.ModuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("module")
@Api(value = "模型管理",description = "应用模块模型管理")
public class ModuleController {
    private final Logger logger = LoggerFactory.getLogger(ModuleController.class);

    @Autowired
    private ModuleService moduleService;

    @ApiOperation(value = "创建模型",httpMethod = "POST")
    @RequestMapping(value= "/create")
    @ResponseBody
    public boolean create(@RequestParam("modulename") String modulename,
                       @RequestParam("moduletype") int moduletype,
                       @RequestParam("owner") long owner,
                       @RequestParam("createtime") String createtime, HttpServletResponse response){
        Module module = new Module();
        module.setModelname(modulename);
        module.setModeltype(moduletype);
        module.setOwner(owner);
        module.setCreateTime(createtime);
        moduleService.create(module);
        return true;
    }

    @ApiOperation(value = "模型查询",httpMethod = "POST")
    @RequestMapping(value= "/findByUser")
    @ResponseBody
    public List<Module> findModuleByUser( @RequestParam("owner") String owner,HttpServletResponse response){
        return moduleService.findByUser(owner);
    }

    @ApiOperation(value = "模型查询",httpMethod = "POST")
    @RequestMapping(value= "/findModels")
    @ResponseBody
    public List<Module> findModules(@RequestParam("startData") String startData,
                                    @RequestParam("endData") String endData,
                                    @RequestParam("type") String type,
                                    @RequestParam("owner") String owner,HttpServletResponse response){
        return moduleService.findModels(startData,endData,type,owner);
    }

    @ApiOperation(value = "删除模型",httpMethod = "POST")
    @RequestMapping(value= "/delById")
    @ResponseBody
    public long delModule( @RequestParam("id") String id,@RequestParam("owner") String owner,HttpServletResponse response){
        return moduleService.delModule(id,owner);
    }
}
