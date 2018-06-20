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
            System.out.println("执行逻辑回归计划:");
           /* dataConfig.setJsondata(DataConfig.getDataConfig().getJsondata());*/
            String pipe = dataConfig.getJsondata();
            String url = Constants.PY_SERVER+dataConfig.getPath();
            System.out.println(pipe);
            System.out.println(url);

            /*String pipe = "{\"appName\": \"testLD\", \"filePath\": \"hdfs://172.16.31.231:9000/data\",\"isSplitSample\":1," +
                    "\"trainRatio\":0.6,\"evaluator\":\"MulticlassClassificationEvaluator\"," +
                    "\"originalStages\": {\"Tokenizer\": {\"inputCol\": \"content\",\"outputCol\": \"words\"}," +
                    "\"HashingTF\": {\"inputCol\": \"words\",\"outputCol\": \"features\"}," +
                    "\"LogisticRegression\": {\"maxIter\": 10,\"regParam\": 0.001}}}";
            String url = "http://localhost:3001/LRDemo";*/

            JavaRequestPythonService jrps = new JavaRequestPythonService();
            jrps.requestPythonService(pipe,url);

        }else if ("end".equals(eventName)) {
            System.out.println("end=========");
        }

    }
}
