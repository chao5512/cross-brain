package com.bonc.pezy.dataconfig;

import com.bonc.pezy.service.AppService;
import com.bonc.pezy.service.NodeService;

/**
 * Created by 冯刚 on 2018/7/14.
 */
public class ServiceMap {

    private static ServiceMap serviceMap = null;

    private ServiceMap(){

    }

    public static ServiceMap getServiceMap(){

        if(null == serviceMap){
            serviceMap = new ServiceMap();
        }
        return serviceMap;
    }

    private AppService appService;

    private NodeService nodeService;

    public AppService getAppService() {
        return appService;
    }

    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}

