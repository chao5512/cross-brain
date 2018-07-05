package com.dataset.management.util;

public class TableTypeTransform {
    public static String getFileType(String fileClass){
        if(fileClass.equalsIgnoreCase("org.apache.hadoop.mapred.TextInputFormat")){
            return "textfile";
        }else if(fileClass.equalsIgnoreCase("org.apache.hadoop.hive.ql.io.orc.OrcInputFormat")){
            return "orc";
        }else if(fileClass.equalsIgnoreCase("org.apache.hadoop.hive.ql.io.RCFileInputFormat")){
            return "rcfile";
        }else if(fileClass.equalsIgnoreCase("org.apache.hadoop.mapred.SequenceFileInputFormat")){
            return "sequencefile";
        }else {
            return null;
        }
    }
}
