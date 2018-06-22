package com.bonc.pezy.algorithmmodel.classification;

import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.dataconfig.DataConfig;
import com.bonc.pezy.pyapi.JavaRequestPythonService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.io.Serializable;

/**
 * Created by 冯刚 on 2018/6/13.
 */
public class LRExectuionListener implements Serializable, ExecutionListener{



    private static final long serialVersionUID = 8513750196548027535L;



    private  DataConfig dataConfig = DataConfig.getDataConfig();

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String eventName = execution.getEventName();
        if ("start".equals(eventName)) {
            System.out.println("start=========");

            String pipe = dataConfig.getJsondata();
            /*String url = Constants.PY_SERVER+dataConfig.getPath();*/
            String url = Constants.PY_SERVER_DEEP+dataConfig.getPath();

            System.out.println(pipe);
            System.out.println(url);
            JavaRequestPythonService jrps = new JavaRequestPythonService();
            jrps.requestPythonService(pipe,url);

        }else if ("end".equals(eventName)) {
            System.out.println("end=========");
        }

    }
}
