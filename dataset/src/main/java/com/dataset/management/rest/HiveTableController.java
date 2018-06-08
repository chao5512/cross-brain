package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;
import com.dataset.management.service.DataSetMetastoreService;
import com.dataset.management.service.HiveTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName HiveTableController
 * @Description 元数据控制类
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
@RestController
@RequestMapping("hive")
@CrossOrigin
public class HiveTableController {
    @Autowired
    private HiveTableService hiveTableService;
    @Autowired
    private DataSetMetastoreService metastoreService;

    /**
     * 功能描述:创建或修改表
     * @param tableMeta
     * @param userId
     * @param dataSetId
     * @return: com.dataset.management.common.ApiResult
     * @auther: 王培文
     * @date: 2018/6/5 16:04
     */
    @RequestMapping(value = "create/{userId}/{dataSetId}",method = RequestMethod.POST)
    public ApiResult createOrUpdateTable(HiveTableMeta tableMeta, @PathVariable("userId") String userId, @PathVariable("dataSetId") String dataSetId){
        User user = new User();
        long id = Long.parseLong(userId);
        user.setId(id);
        DataSet dataSet = new DataSet();
        dataSet.setId(Integer.parseInt(dataSetId));


        boolean table = hiveTableService.createTable(tableMeta, user, dataSet);
        if(table){
            return ResultUtil.success();
        }else {
            return ResultUtil.error(-1,"表已经存在");
        }
    }

    /**
     * 功能描述:获取表元数据信息
     * @param datasetId
     * @return: com.dataset.management.common.ApiResult
     * @auther: 王培文
     * @date: 2018/6/5 16:05
     */
    @RequestMapping(value = "getTableMeta/{datasetId}")
    public ApiResult getHiveTableMeta(@PathVariable("datasetId") String datasetId){
        DataSet dataSet = new DataSet();
        dataSet.setId(Integer.parseInt(datasetId));
        HiveTableMeta hiveTableMeta = metastoreService.getHiveTableMeta(dataSet);
        return ResultUtil.success(hiveTableMeta);
    }

    @RequestMapping(value = "update/{userId}")
    public ApiResult updateHiveTable(HiveTableMeta tableMeta,@PathVariable("userId") String userId){
        DataSet dataSet = new DataSet();
        dataSet.setId(Integer.parseInt(userId));
        boolean result = hiveTableService.alterTableStructure(tableMeta, dataSet);
        if(result){
            return ResultUtil.success();
        }
        return ResultUtil.error(-1,"更新失败");
    }
}
