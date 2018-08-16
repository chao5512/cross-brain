package com.bonc.pezy.util;


import com.bonc.pezy.config.HdfsConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

public class Upload {

    //hadoop默认文件系统
    public static final String FS_DEFAULT_FS = SpringUtil.getBean(HdfsConfig.class).getDefaultFs();

    //host主机地址
    public static final String HDFS_HOST = SpringUtil.getBean(HdfsConfig.class).getHost();

    private static final Logger logger = LoggerFactory.getLogger(Upload.class);

    //完成文件系统FileSystem的初始化

    public static FileSystem fs = null;

    /**
     * 功能描述: 获得hadoop文件系统对象
     * @param
     * @return: org.apache.hadoop.fs.FileSystem
     * @auther: 王培文
     * @date: 2018/8/16 10:18
     */
    public static FileSystem getFileSystem() throws IOException {
        if (fs == null){
            try {
                //创建文件配置对象
                Configuration conf = new Configuration();
                conf.set(FS_DEFAULT_FS, HDFS_HOST);
                //获取文件系统
                fs = FileSystem.get(conf);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return fs;
    }

    /**
     * 功能描述:文件上传
     * @param sourceFileName
     * @param destPath
     * @return: java.lang.String
     * @auther: 王培文
     * @date: 2018/8/16 10:19
     */
    public static String uploadFile(String sourceFileName, String destPath) throws IOException {
        logger.info("原始文件名："+sourceFileName);

        Path source = new Path(sourceFileName);

        //获取文件系统
        FileSystem fs = getFileSystem();

        Path path = new Path(destPath);

        //如果文件夹不存在则创建
        if (!fs.exists(path) || (fs.exists(path)&&!fs.isDirectory(path))) {
            logger.info("文件夹不存在");
            fs.mkdirs(path);
            logger.info("文件夹创建成功");
        }

        //判断文件夹下文件是否存在
        Path fileName = new Path(destPath + "/" + sourceFileName);

        //最终上传到目的地文件名字
        String lastFileName = null;

        if(fs.exists(fileName)){
            StringBuffer sb = new StringBuffer("");
            //获取文件后缀名
            String prefixFileName = sourceFileName.substring(0, sourceFileName.lastIndexOf("."));
            logger.info("文件名前缀："+prefixFileName);
            String suffixFileName = sourceFileName.substring(sourceFileName.lastIndexOf("."), sourceFileName.length());
            logger.info("文件名后缀："+suffixFileName);
            //产生随机数用于拼接在文件名后
            String random = DateUtils.formatDateToString(new Date(), DateUtils.DATE_FORMAT_TIME_YMDHMS);
            //最终文件名
            lastFileName = prefixFileName + "_" + random + suffixFileName;
        }else {
            lastFileName = sourceFileName;
        }

        logger.info("上传到hdfs文件名字:"+lastFileName);

        //构造最终的上传路径
        String destStr = path.getParent()+"/"+path.getName() + "/" + lastFileName;
        logger.info("最终上传路径："+destStr);
        Path destFilePath = new Path(destStr);
        //上传路径
        fs.copyFromLocalFile(true, false, source, destFilePath);

        return destStr;

    }
}
