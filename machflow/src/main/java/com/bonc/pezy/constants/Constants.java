package com.bonc.pezy.constants;

/**
 * Created by 冯刚 on 2018/6/14.
 */
public class Constants {

    //监听器类型
    public static final String LISTENER_E = "activiti:executionListener";

    public static final String LISTENER_U = "activiti:taskListener";

    //python微服务url 机器学习
    public static final String PY_SERVER = "http://47.105.127.125:3001/machinelearning/execute";

    public static final String PY_SERVER_DEEP = "http://47.105.127.125:3002/deeplearning/execute";

    //监听类
    public static final String LR_LISTENER_U = "com.bonc.pezy.algorithmmodel.classification.LRUserTask";

    public static final String LR_REGRESSION = "com.bonc.pezy.algorithmmodel.classification.LRExectuionListener";

    //xml输出文件路径
    public static final String RESOURCE_PATH = "/Users/fenggang/job/AI/AIStidio/cross-brain/machflow/src/main/resources/";
}
