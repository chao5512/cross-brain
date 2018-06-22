package com.dataset.management.SecondaryDao.impl;

import com.dataset.management.SecondaryDao.DataSetMetastoreRepository;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @ClassName DataSetMetastoreRepostoryImpl
 * @Description 操作元数据myql实体类
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
@Repository
public class DataSetMetastoreRepostoryImpl implements DataSetMetastoreRepository{
    @Resource(name = "secondaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public boolean isExistLineDelim(String tableName){
        Boolean result = jdbcTemplate.execute(new StatementCallback<Boolean>() {

            @Override
            public Boolean doInStatement(Statement statement) throws SQLException, DataAccessException {
                /*SELECT 1 FROM DUAL WHERE 'line.delim' IN
                    (SELECT PARAM_KEY
                    FROM TBLS T
                    LEFT JOIN SDS S
                    ON T.SD_ID = S.SD_ID
                    LEFT JOIN SERDE_PARAMS P
                    ON S.SERDE_ID = P.SERDE_ID
                    WHERE T.TBL_NAME = '203_titanic_orc')*/
                StringBuffer sb = new StringBuffer("");
                sb.append("SELECT 1 FROM DUAL WHERE 'line.delim' IN\n" +
                        "(SELECT PARAM_KEY\n" +
                        "FROM TBLS T\n" +
                        "LEFT JOIN SDS S\n" +
                        "ON T.SD_ID = S.SD_ID\n" +
                        "LEFT JOIN SERDE_PARAMS P\n" +
                        "ON S.SERDE_ID = P.SERDE_ID\n" +
                        "WHERE T.TBL_NAME = '");
                sb.append(tableName);
                sb.append("')");
                String sql = sb.toString();
                System.out.println(sql);
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.first()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        return result;
    }
    /**
     * 功能描述:获得表的元数据信息
     * @param tableName
     * @return: com.dataset.management.entity.HiveTableMeta
     * @auther: 王培文
     * @date: 2018/6/5 16:01
     */
    @Override
    public HiveTableMeta getHiveTableMeta(String tableName) {
        HiveTableMeta tableMeta = jdbcTemplate.execute(new StatementCallback<HiveTableMeta>() {
            @Override
            public HiveTableMeta doInStatement(Statement statement) throws SQLException, DataAccessException {
                StringBuffer sb = new StringBuffer("");
                boolean existLineDelim = isExistLineDelim(tableName);
                if(!existLineDelim){
                    sb.append("SELECT T.TBL_NAME,P.PARAM_VALUE TABLE_COMMENT,C.COLUMN_NAME,C.TYPE_NAME,C.COMMENT COLUMN_COMMENT,D1.PARAM_VALUE FIELD_DELIM,PT.PART_NAME\n" +
                            "FROM TBLS T " +
                            "LEFT JOIN TABLE_PARAMS P " +
                            "ON T.TBL_ID = P.TBL_ID " +
                            "LEFT JOIN SDS S " +
                            "ON T.SD_ID = S.SD_ID " +
                            "LEFT JOIN COLUMNS_V2 C " +
                            "ON S.CD_ID = C.CD_ID " +
                            "LEFT JOIN PARTITIONS PT " +
                            "ON T.TBL_ID = PT.TBL_ID " +
                            "LEFT JOIN SERDE_PARAMS D1 " +
                            "ON S.SERDE_ID = D1.SERDE_ID " +
                            "WHERE P.PARAM_KEY = 'comment' " +
                            "AND D1.PARAM_KEY = 'field.delim' " +
                            "AND T.TBL_NAME = '");
                }else{
                    sb.append("SELECT T.TBL_NAME,P.PARAM_VALUE TABLE_COMMENT,C.COLUMN_NAME," +
                            "C.TYPE_NAME,C.COMMENT COLUMN_COMMENT,D1.PARAM_VALUE FIELD_DELIM," +
                            "D2.PARAM_VALUE LINE_DELIM,PT.PART_NAME " +
                            "FROM TBLS T " +
                            "JOIN TABLE_PARAMS P " +
                            "JOIN SDS S " +
                            "JOIN COLUMNS_V2 C " +
                            "JOIN PARTITIONS PT " +
                            "JOIN SERDE_PARAMS D1 " +
                            "JOIN SERDE_PARAMS D2 " +
                            "WHERE T.TBL_ID = P.TBL_ID " +
                            "AND T.SD_ID = S.SD_ID " +
                            "AND S.CD_ID = C.CD_ID " +
                            "AND T.TBL_ID = PT.TBL_ID " +
                            "AND S.SERDE_ID = D1.SERDE_ID " +
                            "AND S.SERDE_ID = D2.SERDE_ID " +
                            "AND P.PARAM_KEY = 'comment' " +
                            "AND D1.PARAM_KEY = 'field.delim' " +
                            "AND D2.PARAM_KEY = 'line.delim' " +
                            "AND T.TBL_NAME = '");
                }
                sb.append(tableName);
                sb.append("'");
                String sql = sb.toString();
                System.out.println(sql);
                ResultSet resultSet = statement.executeQuery(sql);
                HiveTableMeta tableMeta = new HiveTableMeta();
                ArrayList<FieldMeta> fieldMetas = new ArrayList<>();
                int n = 0;
                while (resultSet.next()) {
                    if (n == 0) {
                        tableMeta.setTableName(resultSet.getString("TBL_NAME"));
                        tableMeta.setTableComment(resultSet.getString("TABLE_COMMENT"));
                        tableMeta.setFieldDelim(StringEscapeUtils.escapeJava(resultSet.getString("FIELD_DELIM")));
                        if (existLineDelim){
                            tableMeta.setLineDelim(StringEscapeUtils.escapeJava(resultSet.getString("LINE_DELIM")));
                        }else {
                            tableMeta.setLineDelim("");
                        }
                        FieldMeta fieldMeta = new FieldMeta();
                        fieldMeta.setFieldName(resultSet.getString("COLUMN_NAME"));
                        fieldMeta.setFieldType(resultSet.getString("TYPE_NAME"));
                        fieldMeta.setFieldComment(resultSet.getString("COLUMN_COMMENT"));
                        fieldMetas.add(fieldMeta);
                        n++;
                    } else {
                        FieldMeta fieldMeta = new FieldMeta();
                        fieldMeta.setFieldName(resultSet.getString("COLUMN_NAME"));
                        fieldMeta.setFieldType(resultSet.getString("TYPE_NAME"));
                        fieldMeta.setFieldComment(resultSet.getString("COLUMN_COMMENT"));
                        fieldMetas.add(fieldMeta);
                    }
                }
                tableMeta.setFields(fieldMetas);
                return tableMeta;
            }
        });
        return tableMeta;
    }
}
