package com.dataset.management;

import com.dataset.management.SecondaryDao.HiveRepository;
import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.FieldMeta;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;
import com.dataset.management.service.DataSetMetastoreService;
import com.dataset.management.service.HiveTableService;
import com.dataset.management.util.DateUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration
public class MysqlCrudTest {
    @Resource(name = "secondaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HiveTableService hiveTableService;
    @Autowired
    private DataSetMetastoreService metastoreService;
    @Resource(name = "hiveJdbcTemplate")
    private JdbcTemplate hiveJdbcTemplate;
    @Resource
    private HiveRepository hiveRepository;
    @Resource
    private Environment env;
    @Test
    public void test02(){
//        System.out.println(env.getRequiredProperty("spring.secondary-datasource.url"));
        /*String s = StringEscapeUtils.escapeJava("\t");
        System.out.println(s);*/
        //3_studentno
        String name = "3_studentno";
        int length = name.length();
        int i = name.indexOf("_");
        System.out.println("下标："+i);
        String substring = name.substring(i + 1, length);
//        String[] split = name.split("_");
        System.out.println(substring);
    }
    @Test
    public void test01(){
        jdbcTemplate.execute(new StatementCallback<String>() {
            @Override
            public String doInStatement(Statement statement) throws SQLException, DataAccessException {
                String sql = "SELECT T.TBL_NAME,P.PARAM_VALUE TABLE_COMMENT,C.COLUMN_NAME,C.TYPE_NAME," +
                        "C.COMMENT COLUMN_COMMENT,D1.PARAM_VALUE FIELD_DELIM,D2.PARAM_VALUE LINE_DELIM " +
                        "FROM TBLS T " +
                        "JOIN TABLE_PARAMS P " +
                        "JOIN SDS S " +
                        "JOIN COLUMNS_V2 C " +
                        "JOIN SERDE_PARAMS D1 " +
                        "JOIN SERDE_PARAMS D2 " +
                        "WHERE T.TBL_ID = P.TBL_ID " +
                        "AND T.SD_ID = S.SD_ID " +
                        "AND S.CD_ID = C.CD_ID " +
                        "AND S.SERDE_ID = D1.SERDE_ID " +
                        "AND S.SERDE_ID = D2.SERDE_ID " +
                        "AND P.PARAM_KEY = 'comment' " +
                        "AND D1.PARAM_KEY = 'field.delim' " +
                        "AND D2.PARAM_KEY = 'line.delim' " +
                        "AND T.TBL_NAME = 'studentno'";
                ResultSet resultSet = statement.executeQuery(sql);
                String resultStr = null;
                while (resultSet.next()){
                    resultStr= resultSet.getString("TBL_NAME")+"\t"
                            +resultSet.getString("TABLE_COMMENT")+"\t"
                            +resultSet.getString("COLUMN_NAME")+"\t"
                            +resultSet.getString("TYPE_NAME")+"\t"
                            +resultSet.getString("COLUMN_COMMENT")+"\t"
                            + StringEscapeUtils.escapeJava(resultSet.getString("FIELD_DELIM"))+"\t"
                            +StringEscapeUtils.escapeJava(resultSet.getString("LINE_DELIM"));
                    System.out.println(resultStr);
                }

                return null;
            }
        });
    }

    @Test
    public void test03(){
        HiveTableMeta tableMeta = new HiveTableMeta();
        tableMeta.setTableName("student01");
        tableMeta.setTableComment("学生表");
        tableMeta.setFieldDelim(StringEscapeUtils.escapeJava("\t"));
        tableMeta.setLineDelim(StringEscapeUtils.escapeJava("\n"));
        FieldMeta fieldMeta1 = new FieldMeta();
        fieldMeta1.setFieldName("stuno");
        fieldMeta1.setFieldType("int");
        fieldMeta1.setFieldComment("学号");
        FieldMeta fieldMeta2 = new FieldMeta();
        fieldMeta2.setFieldName("stuname");
        fieldMeta2.setFieldType("string");
        fieldMeta2.setFieldComment("学生姓名");
        ArrayList<FieldMeta> fieldMetas = new ArrayList<>();
        fieldMetas.add(fieldMeta1);
        fieldMetas.add(fieldMeta2);
        tableMeta.setFields(fieldMetas);
        StringBuffer sb = new StringBuffer("");
        sb.append("create external table if not exists ");
        sb.append(tableMeta.getTableName()+"(");
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
        sb.append("/tmp/user/"+001+"'");
        System.out.println(sb.toString());
    }

    @Test
    public void test04(){
        HiveTableMeta tableMeta = new HiveTableMeta();
        tableMeta.setTableName("student01");
        tableMeta.setTableComment("学生表");
        tableMeta.setFieldDelim(StringEscapeUtils.escapeJava("\t"));
        tableMeta.setLineDelim(StringEscapeUtils.escapeJava("\n"));
        FieldMeta fieldMeta1 = new FieldMeta();
        fieldMeta1.setFieldName("stuno");
        fieldMeta1.setFieldType("int");
        fieldMeta1.setFieldComment("学号");
        FieldMeta fieldMeta2 = new FieldMeta();
        fieldMeta2.setFieldName("stuname");
        fieldMeta2.setFieldType("string");
        fieldMeta2.setFieldComment("学生姓名");
        ArrayList<FieldMeta> fieldMetas = new ArrayList<>();
        fieldMetas.add(fieldMeta1);
        fieldMetas.add(fieldMeta2);
        tableMeta.setFields(fieldMetas);
        User user = new User();
        user.setId(new Long(1));
        DataSet dataSet = new DataSet();
        dataSet.setId(2);
        System.out.println(hiveTableService.createTable(tableMeta,user,dataSet));
    }

    @Test
    public void test05(){
        DataSet dataSet = new DataSet();
        dataSet.setId(2);
        HiveTableMeta hiveTableMeta = metastoreService.getHiveTableMeta(dataSet);
        System.out.println("表名："+hiveTableMeta.getTableName());
        System.out.println("表注释："+hiveTableMeta.getTableComment());
        System.out.println("行分割符："+hiveTableMeta.getLineDelim());
        System.out.println("列分割符："+hiveTableMeta.getFieldDelim());
        List<FieldMeta> fields = hiveTableMeta.getFields();
        for (FieldMeta field:fields) {
            System.out.println("字段名："+field.getFieldName());
            System.out.println("字段类型："+field.getFieldType());
            System.out.println("字段字段注释："+field.getFieldComment());
        }
    }

    @Test
    public void load(){
        String formatDateToString = DateUtils.formatDateToString(new Date(),DateUtils.DATE_FORMAT_YMD);
        String sql = "load data inpath '/tmp/student.txt' into table 2_student01 partition (dt = '"+formatDateToString+"')";
        hiveJdbcTemplate.execute(sql);
    }

    @Test
    public void alterTableStructure(){
        DataSet dataSet = new DataSet();
        dataSet.setId(2);
        HiveTableMeta tableMeta = new HiveTableMeta();
        tableMeta.setTableName("student");
        tableMeta.setTableComment("xueshengbiao");
        tableMeta.setFieldDelim(StringEscapeUtils.escapeJava("\t"));
        tableMeta.setLineDelim(StringEscapeUtils.escapeJava("\n"));
        FieldMeta fieldMeta1 = new FieldMeta();
        fieldMeta1.setFieldName("no");
        fieldMeta1.setFieldType("int");
        fieldMeta1.setFieldComment("xuehao");
        FieldMeta fieldMeta2 = new FieldMeta();
        fieldMeta2.setFieldName("name");
        fieldMeta2.setFieldType("string");
        fieldMeta2.setFieldComment("xingmign");
        ArrayList<FieldMeta> fieldMetas = new ArrayList<>();
        fieldMetas.add(fieldMeta1);
        fieldMetas.add(fieldMeta2);
        tableMeta.setFields(fieldMetas);
        hiveTableService.alterTableStructure(tableMeta,dataSet);
    }

    @Test
    public void isExist(){
        DataSet dataSet = new DataSet();
        dataSet.setId(2);
        System.out.println(hiveRepository.isExist(dataSet));
    }
}
