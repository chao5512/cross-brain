package com.dataset.management.controller;

import com.alibaba.fastjson.JSONObject;
import com.dataset.management.aop.annotation.PreventRepetitionAnnotation;
import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.config.HdfsConfig;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;
import com.dataset.management.service.DataSetMetastoreService;
import com.dataset.management.service.DataSetService;
import com.dataset.management.service.HiveTableService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HiveTableController
 * @Description 元数据控制类
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
@RestController
@RequestMapping("hive")
public class HiveTableController {

    private static Logger logger = LoggerFactory.getLogger(DataSetController.class);

    @Autowired
    private HiveTableService hiveTableService;
    @Autowired
    private DataSetMetastoreService metastoreService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private HdfsConfig hdfsConfig;

    @RequestMapping(value = "uuid",method = RequestMethod.GET)
    @PreventRepetitionAnnotation
    public ApiResult toUuid(HttpServletRequest request){
        return ResultUtil.success();
    }

    /**
     * 功能描述:创建或修改表
     * @param jsonData
     * @return: com.dataset.management.common.ApiResult
     * @auther: 王培文
     * @date: 2018/8/28 15:25
     */
    @PreventRepetitionAnnotation
    @RequestMapping(value = "save",method = RequestMethod.POST)
    public ApiResult createOrUpdateTableAndPreventRepeat(@RequestBody String jsonData){

        try {
            //解析json
            logger.info(jsonData);
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            long userId = jsonObject.getLongValue("userId");
            logger.info("userId: " + userId);
            int dataSetId = jsonObject.getIntValue("dataSetId");
            logger.info("dataSetId: " + dataSetId);
            String token = jsonObject.getString("token");
            logger.info("token: " + token);
            HiveTableMeta tableMeta = jsonObject.getObject("tableMeta", HiveTableMeta.class);
            User user = new User();
            user.setId(userId);
            DataSet dataSet = new DataSet();
            dataSet.setId(dataSetId);
            boolean exist = hiveTableService.isExist(dataSet);
            if(exist){
                try {
                    boolean result = hiveTableService.alterTableStructure(tableMeta, user,dataSet);
                    if(result){
                        logger.info("表更新成功");
                        return ResultUtil.success();
                    }
                    logger.error("更新失败");
                    return ResultUtil.error(2021,"更新失败");
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error(String.valueOf(e.getStackTrace()));
                    return ResultUtil.error(2021,"更新失败");
                }
            }else{
                boolean table = false;
                try {
                    table = hiveTableService.createTable(tableMeta, user, dataSet);
                    if(table){
                        logger.info("创建成功");
                        return ResultUtil.success();
                    }else {
                        logger.error("创建失败,表已经存在");
                        return ResultUtil.error(2022,"表已经存在");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(String.valueOf(e.getStackTrace()));
                    return ResultUtil.error(2023,"创建表失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.valueOf(e.getStackTrace()));
            return ResultUtil.error(2024,"参数异常");
        }
    }

    /**
     * 功能描述:获取表元数据信息
     * @param hiveTableName
     * @return: com.dataset.management.common.ApiResult
     * @auther: 王培文
     * @date: 2018/6/5 16:05
     */
    @RequestMapping(value = "getTableMeta",method = RequestMethod.POST)
    public ApiResult getHiveTableMeta(@RequestParam("hivetablename") String hiveTableName){
        try {
            HiveTableMeta hiveTableMeta = metastoreService.getHiveTableMeta(hiveTableName);
            logger.info("查询成功");
            return ResultUtil.success(hiveTableMeta);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.valueOf(e.getStackTrace()));
            return ResultUtil.error(2025,"获取表信息失败");
        }
    }

}
