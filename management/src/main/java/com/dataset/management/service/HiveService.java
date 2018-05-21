package com.dataset.management.service;

import com.dataset.management.consts.DataSetSystemConsts;
import com.dataset.management.entity.DataSetColumn;
import com.dataset.management.entity.Hiveinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class HiveService {

    private Logger logger = LoggerFactory.getLogger(HiveService.class);

    private static final String drives = DataSetSystemConsts.HIVE_DRIVE_NAME;
    private static final String driveUrl = DataSetSystemConsts.HIVE_DRIVE_URL;
    private static final String hiveuser = DataSetSystemConsts.HIVE_USER;
    private static final String userpassword =DataSetSystemConsts.HIVE_PASSWORD;
    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;
    private static Hiveinfo hiveinfo;

    HiveService(){}

    public void beforsql() throws Exception{
        String newDriverurl = driveUrl+ "/default";
        Class.forName(drives);
        conn = DriverManager.getConnection(newDriverurl,hiveuser,userpassword);
        stmt = conn.createStatement();
    }

    public void close() throws Exception{
        if ( rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

    public void createDataBase() throws Exception{
        beforsql();
        String userName = hiveuser;
        String SQL = "create database IF NOT EXISTS "+hiveuser;
        logger.info(SQL);
        stmt.execute(SQL);
        logger.info("create database success!");
        close();
    }

    public void createHiveTable() throws Exception{
        String newDriveurl = driveUrl+hiveuser;
        Class.forName(drives);
        conn = DriverManager.getConnection(newDriveurl,hiveuser,userpassword);
        stmt = conn.createStatement();
        logger.info("connect "+hiveuser+ "database success ");
        List<DataSetColumn> columns = hiveinfo.getColumninfos();
        String sql = "create external table if not exists "+hiveinfo.getHiveTableNmae()+"("+getcloumninfo(columns)
                +")"+getTerminaterStr(hiveinfo.getfTerminatedby(),hiveinfo.getlTerminaterby())+getStoreAs(hiveinfo.getStoreAs());
        stmt.execute(sql);
        close();
    }

    public void dropHiveTable() throws Exception{
        String newDriveurl = driveUrl+hiveuser;
        Class.forName(drives);
        conn = DriverManager.getConnection(newDriveurl,hiveuser,userpassword);
        stmt = conn.createStatement();
        String sql = "drop table if exists"+hiveuser;
        stmt.execute(sql);
    }

    public  String getcloumninfo(List<DataSetColumn> columns ) {
        String columnString =null;
        for(int num=0;num < columns.size();num++){
         String columnName = columns.get(num).getColumnName();
         String columnType = columns.get(num).getDatatype();
         String columnDesc = columns.get(num).getComment();
         if(columnDesc!=null){
             while (num ==(columns.size()-1)){
                 columnString += columnName +" "+columnType+" "+"comment "+" \""+columnDesc+"\"";
             }
             columnString += columnName +" "+columnType+" "+"comment "+" \""+columnDesc+"\",\n";
         }
            while (num ==(columns.size()-1)){
                columnString += columnName +" "+columnType+" ,";
            }
            columnString += columnName +" "+columnType+" ,\n";
         }
        return columnString;
    }

    public  String getTerminaterStr(String ftem,String ltem){
        String terStr = " row format delimited fields terminated by \""+ftem+"\""+" lines terminated by \""+ltem+"\"";
        return terStr;
    }

    public  String getStoreAs(String storeAs){
        String storeAsStr = " stored  as "+storeAs;
        return storeAsStr;
    }
    public  Hiveinfo getHiveinfo() {
        return hiveinfo;
    }
    public  void setHiveinfo(Hiveinfo hiveinfo) {
        HiveService.hiveinfo = hiveinfo;
    }

    public String getHiveTableName() {
        return hiveuser;
    }

}
