package com.dataset.management.consts;

import sun.dc.pr.PRError;

import javax.swing.plaf.PanelUI;
import java.lang.ref.PhantomReference;

public class DataSetSystemConsts {

    public static String DATASET_USER = "machen";

    public static String HIVE_USER = "";

    public static String HIVE_PASSWORD ="";

    public static String DATASET_OWNER ="MACHEN";

    public static String DATASET_SYSTEM_TABLENAME = "dataset_system_info";

    public static String DATASET_BASIC_INFO = "dataset_basic_info";

    public static String DATASET_FILES_INFO = "dataset_files_info";

    public static String HIVE_DRIVE_NAME = "org.apache.hive.jdbc.HiveDriver";

    public static String  HIVE_DRIVE_URL= "jdbc:hive2://xxxxxx:10000";

    public static String HDFS_BASIC_URL ="hdfs:xxxxxxx:9000/";




    public DataSetSystemConsts(){}

    //使用者
    public static String getDatasetUser() {
        return DATASET_USER;
    }

    public void setDatasetUser(String dtsUserName){
        DATASET_USER = dtsUserName;
    }

    //创建者
    public static String getDatasetOwner() {
        return DATASET_OWNER;
    }

    public static void setDatasetOwner(String datasetOwner) {
        DATASET_OWNER = datasetOwner;
    }
}
