package com.dataset.management.dao.hivedao.impl;

import com.dataset.management.controller.DataSetController;
import com.dataset.management.dao.hivedao.HiveRepository;
import com.dataset.management.entity.*;
import com.dataset.management.service.IntDataSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName HiveRepositoryImpl
 * @Description 操作hive实体类
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
@Repository
public class HiveRepositoryImpl implements HiveRepository {

    private static Logger logger = LoggerFactory.getLogger(HiveRepositoryImpl.class);

    @Resource(name = "hiveJdbcTemplate")
    private JdbcTemplate hiveJdbcTemplate;

    @Resource(name = "secondaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IntDataSetService dataSetOptService;


    /*@Value("${hadoop.hive-path}")
    private String hivePath;*/

    /**
     * 功能描述:创建表
     * @param tableMeta
     * @param user
     * @param dataSet
     * @return: void
     * @auther: 王培文
     * @date: 2018/6/5 15:58
     */
    @Override
    public boolean createTable(HiveTableMeta tableMeta, User user, DataSet dataSet){
//        tableMeta.getLineDelim()==""
        try {
            StringBuffer sb = new StringBuffer("");
            sb.append("create external table if not exists ");
            String tableName = dataSet.getId()+"_"+tableMeta.getTableName();
            sb.append(tableName+"(");
            List<FieldMeta> fields = tableMeta.getFields();
            for (int i = 0; i <fields.size() ; i++) {
                FieldMeta fieldMeta = fields.get(i);
                sb.append(fieldMeta.getFieldName()+" ");
                if(fieldMeta.getFieldType().equals("float")){
                    sb.append("float comment '");
                    sb.append(fieldMeta.getFieldComment()+"'");
                }else{
                    sb.append(fieldMeta.getFieldType()+" comment '");
                    sb.append(fieldMeta.getFieldComment()+"'");
                }
                if (i!=(fields.size()-1)){
                    sb.append(",");
                }
            }
            sb.append(") ");
            sb.append("comment '");
            sb.append(tableMeta.getTableComment()+"' ");
            sb.append("row format delimited fields terminated by '");
            sb.append(tableMeta.getFieldDelim()+"' ");
            //根据是否存在行分隔符进行创建表
            if(tableMeta.getLineDelim()!=""){
                //有行分隔符
                sb.append("lines terminated by '");
                sb.append(tableMeta.getLineDelim()+"' ");
            }else{
                //无行分隔符
            }
            sb.append("stored as ");
            sb.append(tableMeta.getFiletype());
            sb.append(" location '");
            DataSet data = dataSetOptService.findById(dataSet.getId());
            sb.append(data.getDataSetStoreUrl()+"'");
            String sql = sb.toString();
            logger.info("建表语句：" + sql);
            hiveJdbcTemplate.execute(sql);
            logger.info("表" + tableName +"创建成功");
            //保存到数据库
            data.setDataSetHiveTableName(tableName);
            dataSetOptService.save(data);
            logger.info("表" + tableName + "更新数据库总的字段成功");
        } catch (DataAccessException e) {
            e.printStackTrace();
            logger.error(String.valueOf(e.getStackTrace()));
            return false;
        }
        return true;
    }

    /**
     * 功能描述:判断表是否存在
     * @param dataSet
     * @return: boolean
     * @auther: 王培文
     * @date: 2018/6/5 15:58
     */
    @Override
    public boolean isExist(DataSet dataSet) {
        //SELECT COUNT(*) FROM TBLS T WHERE T.TBL_NAME LIKE 'people01%'
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT COUNT(*) FROM TBLS T WHERE T.TBL_NAME LIKE ?");
            ArrayList<Object> params = new ArrayList<Object>();
            //SELECT COUNT(*) FROM TBLS T WHERE T.TBL_NAME LIKE '2\_%'
            params.add(dataSet.getId()+"\\_%");
            Integer query = jdbcTemplate.query(sb.toString(), params.toArray(), new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                    int count = 0;
                    if (resultSet.next()) {
                        count = resultSet.getInt(1);
                    }
                    return count;
                }
            });
            if(query == 0){
                return false;
            }else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.valueOf(e.getStackTrace()));
            return false;
        }
    }

    /**
     * 功能描述:根据dataset的id查找表明
     * @param dataSet
     * @return: java.lang.String
     * @auther: 王培文
     * @date: 2018/6/5 15:58
     */
    @Override
    public String getTableNameByDataSet(DataSet dataSet) {
        //SELECT TBL_NAME FROM TBLS T WHERE T.TBL_NAME LIKE '2_%'
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT TBL_NAME FROM TBLS T WHERE T.TBL_NAME LIKE ?");
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(dataSet.getId()+"\\_"+"%");
        String query = jdbcTemplate.query(sb.toString(), params.toArray(), new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                String name = null;
                if (resultSet.next()) {
                    name = resultSet.getString(1);
                }
                return name;
            }
        });
        return query;
    }

    /**
     * 功能描述:更改表结构
     * @param tableMeta
     * @param dataSet
     * @return: boolean
     * @auther: 王培文
     * @date: 2018/6/5 16:08
     */
    @Override
    public boolean alterTableStructure(HiveTableMeta tableMeta, DataSet dataSet) {
        //String sql1 = "ALTER TABLE studentno RENAME TO studentno";
        String oldTableName = getTableNameByDataSet(dataSet);
        String newTableName = dataSet.getId()+"_"+tableMeta.getTableName();
        //拼接更改表名语句
        StringBuffer alterName = new StringBuffer("");
        alterName.append("ALTER TABLE ");
        alterName.append(oldTableName);
        alterName.append(" RENAME TO ");
        alterName.append(newTableName);
        String alterNameSql = alterName.toString();
        System.out.println("更改表名语句："+alterNameSql);
        int alterNameUpdateCount = hiveJdbcTemplate.update(alterNameSql);
        System.out.println("更新表名修改行数："+alterNameUpdateCount);
        //拼接修改表的注释sql语句
        //ALTER TABLE table_name SET TBLPROPERTIES('comment' = new_comment);
        StringBuffer updateTableComment = new StringBuffer("");
        updateTableComment.append("ALTER TABLE ");
        updateTableComment.append(newTableName);
        updateTableComment.append(" SET TBLPROPERTIES('comment'='");
        String tableComment = tableMeta.getTableComment();
        updateTableComment.append(tableComment+"')");
        String updateTableCommentSql = updateTableComment.toString();
        System.out.println("打印修改表注释语句："+updateTableCommentSql);
        int alterTableCommentUpateCount = hiveJdbcTemplate.update(updateTableCommentSql);
        System.out.println("更改表注释影响行数："+alterTableCommentUpateCount);
        //拼接修改表行列分隔符语句
//        alter table studentno1 set SERDEPROPERTIES('field.delim'='aa','line.delim'='bb')
        StringBuffer updateTableDelim = new StringBuffer("");
        updateTableDelim.append("ALTER TABLE ");
        updateTableDelim.append(newTableName);
        updateTableDelim.append(" SET SERDEPROPERTIES('field.delim'=");
        updateTableDelim.append("'"+tableMeta.getFieldDelim()+"'");
        updateTableDelim.append(",'line.delim'=");
        updateTableDelim.append("'"+tableMeta.getLineDelim()+"'");
        updateTableDelim.append(")");
        String updateTableDelimSql = updateTableDelim.toString();
        System.out.println("更改表分隔符语句："+updateTableDelimSql);
        int alterTableDelimUpateCount = hiveJdbcTemplate.update(updateTableDelimSql);
        System.out.println("更改表分隔符影响行数："+alterTableDelimUpateCount);
        //拼接修改字段信息语句
        //ALTER TABLE studentno REPLACE COLUMNS(stuno1 int comment 'xuhao1',stuname1 string comment 'xingming1')
        StringBuffer updateTableFields = new StringBuffer("");
        updateTableFields.append("ALTER TABLE ");
        updateTableFields.append(newTableName);
        updateTableFields.append(" REPLACE COLUMNS(");
        List<FieldMeta> fields = tableMeta.getFields();
        for (int i = 0; i < fields.size(); i++) {
            if(i!=(fields.size()-1)){
                updateTableFields.append(fields.get(i).getFieldName());
                updateTableFields.append(" "+fields.get(i).getFieldType()+" comment ");
                updateTableFields.append("'"+fields.get(i).getFieldComment()+"',");
            }else {
                updateTableFields.append(fields.get(i).getFieldName());
                updateTableFields.append(" "+fields.get(i).getFieldType()+" comment ");
                updateTableFields.append("'"+fields.get(i).getFieldComment()+"')");
            }
        }
        String updateTableFieldsSql = updateTableFields.toString();
        System.out.println("修改表字段信息语句："+updateTableFieldsSql);
        int alterTableFieldsUpateCount = hiveJdbcTemplate.update(updateTableFieldsSql);
        System.out.println("更改表字段信息影响行数："+alterTableFieldsUpateCount);
        //更改表的存储类型
        //ALTER TABLE table_name SET FILEFORMAT file_format
        StringBuffer updateTableStoreType = new StringBuffer("");
        updateTableStoreType.append("ALTER TABLE ");
        updateTableStoreType.append(newTableName);
        updateTableStoreType.append(" SET FILEFORMAT ");
        updateTableStoreType.append(tableMeta.getFiletype());
        String updateTableStoreTypeSql = updateTableStoreType.toString();
        System.out.println("修改表存储类型语句："+updateTableStoreTypeSql);
        int updateTableStoreTypeCount = hiveJdbcTemplate.update(updateTableStoreTypeSql);
        System.out.println("更改表存储类型影响行数："+updateTableStoreTypeCount);
        return true;
    }

    @Override
    public boolean dropTableByName(String name) {
        try {
            logger.info("表名：" + name);
            StringBuffer dropTableStringBuffer = new StringBuffer("");
            dropTableStringBuffer.append("DROP TABLE IF EXISTS ");
            dropTableStringBuffer.append(name);
            String dropTableSql = dropTableStringBuffer.toString();
            logger.info("删除语句：" + dropTableSql);
            hiveJdbcTemplate.update(dropTableSql);
            logger.info("表" + name + "删除成功");
        } catch (DataAccessException e) {
            e.printStackTrace();
            logger.error(String.valueOf(e.getStackTrace()));
            return false;
        }
        return true;
    }
}
