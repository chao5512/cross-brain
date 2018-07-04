package com.dataset.management.rest;

import com.dataset.management.common.ApiResult;
import com.dataset.management.common.ResultUtil;
import com.dataset.management.config.HdfsConfig;
import com.dataset.management.consts.DataSetConsts;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;
import com.dataset.management.service.DataSetMetastoreService;
import com.dataset.management.service.DataSetService;
import com.dataset.management.service.HiveTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    @Autowired
    private HiveTableService hiveTableService;
    @Autowired
    private DataSetMetastoreService metastoreService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private HdfsConfig hdfsConfig;

    /**
     * 功能描述:创建或修改表
     * @param tableMeta
     * @param userId
     * @param dataSetId
     * @return: com.dataset.management.common.ApiResult
     * @auther: 王培文
     * @date: 2018/6/5 16:04
     */
    @RequestMapping(value = "save/{userId}/{dataSetId}",method = RequestMethod.POST)
    public ApiResult createOrUpdateTable(HiveTableMeta tableMeta, @PathVariable("userId") String userId, @PathVariable("dataSetId") String dataSetId){
        User user = new User();
        long id = Long.parseLong(userId);
        user.setId(id);
        DataSet dataSet = new DataSet();
        dataSet.setId(Integer.parseInt(dataSetId));
        boolean exist = hiveTableService.isExist(dataSet);
        if(exist){
            boolean result = hiveTableService.alterTableStructure(tableMeta, dataSet);

            if(result){
                return ResultUtil.success();
            }
            return ResultUtil.error(-1,"更新失败");
        }else{
            boolean table = false;
            try {
                table = hiveTableService.createTable(tableMeta, user, dataSet);
                //更新hive 表名称和其他相关
                DataSet dataSetConTent = dataSetService.findById(Integer.parseInt(dataSetId));
                String hiveTableName = tableMeta.getTableName();
                long timetmp = System.currentTimeMillis();
                String hiveTableID = hiveTableName+"_"+timetmp;
                dataSetConTent.setDataSetHiveTableName(hiveTableName);
                dataSetConTent.setDataSetHiveTableId(hiveTableID);

                String hdfsUrl = hdfsConfig.getHdfsUrl();
                Long hdfsPort = hdfsConfig.getHdfsProt();
                String dataStoreUrl = hdfsUrl+":"+hdfsPort+DataSetConsts.DATASET_STOREURL_DIR
                        +"/"+dataSetConTent.getUserName()+"/"+dataSetConTent.getDataSetName();
                dataSetConTent.setDataSetStoreUrl(dataStoreUrl);
                dataSetService.save(dataSetConTent);

                if(table){
                    return ResultUtil.success();
                }else {
                    return ResultUtil.error(-1,"表已经存在");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResultUtil.error(-1,"更新失败");
            }

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
        String tableName = hiveTableMeta.getTableName();
        int length = tableName.length();
        int index = tableName.indexOf("_");
        String subTableName = tableName.substring(index + 1, length);
        hiveTableMeta.setTableName(subTableName);
        return ResultUtil.success(hiveTableMeta);
    }

}
