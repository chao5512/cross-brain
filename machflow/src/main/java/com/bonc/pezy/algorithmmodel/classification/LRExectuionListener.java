package com.bonc.pezy.algorithmmodel.classification;

import com.alibaba.druid.support.json.JSONUtils;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.dataconfig.AppData;
import com.bonc.pezy.dataconfig.DataConfig;
import com.bonc.pezy.dataconfig.NodeData;
import com.bonc.pezy.dataconfig.NodeSet;
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
    private NodeSet nodeSet = NodeSet.getNodeSet();
    private DataConfig dataConfig = DataConfig.getDataConfig();

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String eventName = execution.getEventName();
        if ("start".equals(eventName)) {
            System.out.println("start=========");
            System.out.println("===xxxx===="+execution.getEventName());

            String url = null;
            Map<String,String> param = new HashMap<String, String>();
            String pipe = null;
            if("ml".equals(appData.getAppType())){
                url = Constants.PY_SERVER;

                param.put("appName",appData.getAppName());

                Map<String, NodeData> nodeMap = nodeSet.getNodeMap();

                nodeMap.forEach((key,value)->{

                    param.put(key,value.getParam());

                });
                pipe = JSONUtils.toJSONString(param);

            }

            if("deep".equals(appData.getAppType())){
                url = Constants.PY_SERVER_DEEP;
                pipe = dataConfig.getJsondata();

            }
            System.out.println(pipe);
            System.out.println(url);
            if (!"".equals(pipe)){
                JavaRequestPythonService jrps = new JavaRequestPythonService();
                jrps.requestPythonService(pipe,url);

            }

        }else if ("end".equals(eventName)) {
            System.out.println("end=========");
            System.out.println("===xxxx===="+execution.getEventName());

        }



    }
}
