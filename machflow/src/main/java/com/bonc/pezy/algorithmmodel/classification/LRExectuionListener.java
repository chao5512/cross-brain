package com.bonc.pezy.algorithmmodel.classification;

import com.alibaba.druid.support.json.JSONUtils;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.dataconfig.ServiceMap;
import com.bonc.pezy.entity.App;
import com.bonc.pezy.entity.Node;
import com.bonc.pezy.pyapi.JavaRequestPythonService;
import com.bonc.pezy.service.AppService;
import com.bonc.pezy.service.NodeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 冯刚 on 2018/6/13.
 */
public class LRExectuionListener implements Serializable, ExecutionListener{



    private static final long serialVersionUID = 8513750196548027535L;

    private ServiceMap serviceMap = ServiceMap.getServiceMap();

    private AppService appService = serviceMap.getAppService();
    private NodeService nodeService = serviceMap.getNodeService();

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String eventName = execution.getEventName();
        String[] p = execution.getProcessDefinitionId().split(":");
        if ("start".equals(eventName)) {
            System.out.println("start=========");
            System.out.println("===xxxx===="+execution.getEventName()+"====yyyyy="+execution.getBusinessKey());
            App app = appService.findByProcessId(p[0],Integer.parseInt(execution.getBusinessKey()));
            String url = null;
            Map<String,String> param = new HashMap<String, String>();
            String pipe = null;
            System.out.println("===xxxx===="+app.getAppType());

            List<Node> nodes = nodeService.findByAppId(app.getAppId());

            if(app.getAppType() == 1){
                url = Constants.PY_SERVER;

                param.put("appName",app.getAppName());
                for(Node node:nodes){

                    param.put(node.getNodeName(),node.getParam());

                }

            }
            if(app.getAppType()==2){
                url = Constants.PY_SERVER_DEEP;
                param.put("appName",app.getAppName());
                for(Node node:nodes){

                    param.put(node.getNodeName(),node.getParam());

                }
            }

            pipe = JSONUtils.toJSONString(param);
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
