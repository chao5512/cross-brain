package com.dataset.management.consts;

import com.dataset.management.entity.DataSet;
import org.omg.CORBA.PUBLIC_MEMBER;

import javax.swing.plaf.PanelUI;

public class DataSetConsts {

    private DataSetConsts(){

    }

    public static final String POWER_PUBLIC = "PUBLIC";

    public static final String POWER_PRIVATE = "PRIVATE";

    public static final String SORT_BY_DATASET_ENGLISH_NAME = "dataSetEnglishName";

    public static final String SORT_BY_DATASET_CREATE_TIME = "dataSetCreateTime";

    public static final String SORT_BY_DATASET_UPDATE_TIME = "dataSetLastUpdateTime";

    public static final String SORTTYPE_AESC = "AESC";

    public static final String SORTTYPE_DESC = "DESC";

    public static final int MAX_CONTENER = 150;

    public static final String UPLOAD_STATUS_LOADING = "LOADING";

    public static final String UPLOAD_STATUS_COMPLETE = "COMPLETE";

    public static final String MYSQL_URL = "jdbc:mysql://localhost:3306/"+ DataSetSystemConsts.getDatasetUser();

    public static final String MYSQL_CHARATER = "?useUnicode=true&characterEncoding=gbk";

    public static final String DATASET_USER = DataSetSystemConsts.DATASET_USER;

    public static final String USER_PASSWORD = "1235";

    public static final String MYSQL_DRIVE = "com.mysql.jdbc.Driver";

}