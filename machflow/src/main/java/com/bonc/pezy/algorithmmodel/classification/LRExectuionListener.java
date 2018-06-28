package com.bonc.pezy.algorithmmodel.classification;

import com.alibaba.druid.support.json.JSONUtils;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.dataconfig.AppData;
import com.bonc.pezy.dataconfig.NodeData;
import com.bonc.pezy.pyapi.JavaRequestPythonService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 冯刚 on 2018/6/13.
 */
public class LRExectuionListener implements Serializable, ExecutionListener{



    private static final long serialVersionUID = 8513750196548027535L;


    private AppData appData = AppData.getAppData();

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String eventName = execution.getEventName();
        if ("start".equals(eventName)) {
            System.out.println("start=========");
            System.out.println("===xxxx===="+execution.getEventName());

            String url = null;
            if("ml".equals(appData.getAppType())){
                url = Constants.PY_SERVER;

            }
            if("deep".equals(appData.getAppType())){
                url = Constants.PY_SERVER_DEEP;
            }

            Map<String,String> param = new HashMap<String, String>();

            param.put("appName",appData.getAppName());

            Map<String, NodeData> nodeMap = appData.getNodeMap();

            nodeMap.forEach((key,value)->{

                param.put(key,value.getParam());

            });

            String pipe =JSONUtils.toJSONString(param);


            /*String pipe = dataConfig.getJsondata();
            String url = Constants.PY_SERVER+dataConfig.getPath();
            String url = Constants.PY_SERVER_DEEP;*/

            System.out.println(pipe);
            System.out.println(url);
            JavaRequestPythonService jrps = new JavaRequestPythonService();
            jrps.requestPythonService(pipe,url);

        }else if ("end".equals(eventName)) {
            System.out.println("end=========");
            System.out.println("===xxxx===="+execution.getEventName());

        }



    }
}
