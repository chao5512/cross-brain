package com.dataset.management.consts;

import org.omg.CORBA.PUBLIC_MEMBER;

public class DataSetFileConsts {
    private DataSetFileConsts(){

    }
    public static final String STATUS_ONLINE = "ONLINE";
    public static final String STATUS_DOWN_LINE = "DOWNLINE";
    public static final String STATUS_WAITING = "WAITING";
    public static final String STATUS_RUNNING = "RUNNING";

    public static final String FILE_SORT_TYPE_AESC = "asc";
    public static final String FILE_SORT_TYPE_DESC="desc";

    public static final String FILE_SORT_BY_FILENAME = "fileName";
    public static final String FILE_SORT_BY_UPLOADTIME= "uploadTime";
}
