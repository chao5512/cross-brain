package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;
import com.dataset.management.service.HiveTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hive")
public class HiveTableController {
    @Autowired
    private HiveTableService hiveTableService;

    @RequestMapping(value = "create/{userId}/{dataSetId}",method = RequestMethod.POST)
    public ApiResult createTable(HiveTableMeta tableMeta, @PathVariable("userId") String userId, @PathVariable("dataSetId") String dataSetId){
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
}
