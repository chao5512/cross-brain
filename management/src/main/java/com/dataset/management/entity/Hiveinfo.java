package com.dataset.management.entity;

import com.dataset.management.consts.DataSetSystemConsts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/******
 *
 * 暂时不写
 *
 * */
@Entity(name = "dataset_hivetable_info")
public class Hiveinfo implements Serializable {
    @Column(nullable = false,name = "user")
    private static String hiveDatabaseName = DataSetSystemConsts.HIVE_USER;
    @Column(nullable = false,name = "hivetableId")
    private static String hiveId;
    @Column(nullable = false,name = "create_time")
    private static String createDate;
    @ManyToOne
    @JoinColumn(name = "datasetId")
    private static  DataSet dataSet;

    @Column(nullable = false,name = "datasetId")
    private static String datasetId;
    @Column(nullable = false,name ="hivetableName")
    private static String hiveTableName;
    @Column(nullable = false,name = "hivetable_path")
    private static String hiveTablePath;
    @Column()
    private static List<DataSetColumn> columninfos;
    @Column(nullable = false,name = "storeAs")
    private String storeAs;

    private static String F_TERMINATEDBY;

    private static String L_TERMINATERBY;

    public String getHiveDatabaseName() {
        return hiveDatabaseName;
    }
    public void setHiveDatabaseName(String hiveDatabaseName) {
        this.hiveDatabaseName = hiveDatabaseName;
    }

    //hive表ID
    public  String getHiveId() {
        return hiveId;
    }
    public  void setHiveId() {
        hiveId = hiveDatabaseName+"_"+getNowTimestamp();
    }

    //俩个时间转换方法
    public long getNowTimestamp(){
        long date = System.currentTimeMillis();
        return date;
    }
    public String getNowTimedate(){
        long date = getNowTimestamp();     //当前时间戳
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(date))));   // 时间戳转换成时间
        return sd;
    }

    public String getCreateDate() {
        createDate = getNowTimedate();
        return createDate;
    }

    //定位数据集
    public DataSet getDataSet() {
        return dataSet;
    }
    public  void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    //数据集ID
    public String getDatasetId() {
        return datasetId;
    }
    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    //hive表名
    public String getHiveTableNmae() {
        return hiveTableName;
    }
    public void setHiveTableNmae(String hiveTableNmae) {
        this.hiveTableName = hiveTableNmae;
    }

    //hive表路径
    public String getHiveTablePath() {
        return hiveTablePath;
    }
    public void setHiveTablePath(String hiveTablePath) {
        this.hiveTablePath = hiveTablePath;
    }

    //列
    public List<DataSetColumn> getColumninfos() {
        return columninfos;
    }
    public void setColumninfos(List<DataSetColumn> columninfos1) {
        columninfos = columninfos1;
    }

    //分隔符
    public String getfTerminatedby() {
        return F_TERMINATEDBY;
    }
    public void setfTerminatedby(String fTerminatedby) {
        F_TERMINATEDBY = fTerminatedby;
    }

    //分隔符
    public String getlTerminaterby() {
        return L_TERMINATERBY;
    }
    public void setlTerminaterby(String lTerminaterby) {
        L_TERMINATERBY = lTerminaterby;
    }


    public String getStoreAs() {
        return storeAs;
    }
    public void setStoreAs(String storeAs) {
        this.storeAs = storeAs;
    }



}
