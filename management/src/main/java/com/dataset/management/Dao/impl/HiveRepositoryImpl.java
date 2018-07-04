package com.dataset.management.Dao.impl;

import com.dataset.management.Dao.HiveRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HiveRepositoryImpl implements HiveRepository {
    @Resource(name = "hiveJdbcTemplate")
    private JdbcTemplate hiveJdbcTemplate;

    @Resource(name = "secondaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Value("${hadoop.hive-path}")
    private String hivePath;

    @Override
    public void createTable(HiveTableMeta tableMeta, User user, DataSet dataSet) {
        StringBuffer sb = new StringBuffer("");
        sb.append("create external table if not exists ");
        String tableName = dataSet.getId()+"_"+tableMeta.getTableName();
        sb.append(tableName+"(");
        List<FieldMeta> fields = tableMeta.getFields();
        for (int i = 0; i <fields.size() ; i++) {
            FieldMeta fieldMeta = fields.get(i);
            sb.append(fieldMeta.getFieldName()+" ");
            sb.append(fieldMeta.getFieldType()+" comment '");
            sb.append(fieldMeta.getFieldComment()+"'");
            if (i!=(fields.size()-1)){
                sb.append(",");
            }
        }
        sb.append(")");
        sb.append("comment '");
        sb.append(tableMeta.getTableComment()+"' ");
        sb.append("partitioned by(dt string) ");
        sb.append("row format delimited fields terminated by '");
        sb.append(tableMeta.getFieldDelim()+"' ");
        sb.append("lines terminated by '");
        sb.append(tableMeta.getLineDelim()+"' ");
        sb.append("stored as textfile ");
        sb.append("location '");
        sb.append(hivePath+user.getId()+"'");
        String sql = sb.toString();
        System.out.println(sql);
        hiveJdbcTemplate.execute(sql);
    }

    @Override
    public boolean isExist(DataSet dataSet) {
        //SELECT COUNT(*) FROM TBLS T WHERE T.TBL_NAME LIKE 'people01%'
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COUNT(*) FROM TBLS T WHERE T.TBL_NAME LIKE ?");
        ArrayList<Object> params = new ArrayList<Object>();
        params.add("%"+dataSet.getId()+"%");
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
    }

    @Override
    public String getTableNameByDataSet(DataSet dataSet) {
        //SELECT TBL_NAME FROM TBLS T WHERE T.TBL_NAME LIKE '2_%'
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT TBL_NAME FROM TBLS T WHERE T.TBL_NAME LIKE ?");
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(dataSet.getId()+"_"+"%");
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
}
