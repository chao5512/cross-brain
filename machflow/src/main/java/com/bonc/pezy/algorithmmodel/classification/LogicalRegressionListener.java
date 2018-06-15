package com.bonc.pezy.algorithmmodel.classification;

import com.bonc.pezy.pyapi.JavaRequestPythonService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

/**
 * Created by 冯刚 on 2018/6/8.
 */
public class LogicalRegressionListener implements TaskListener {

    private Expression arg;

    public Expression getArg() {
        return arg;
    }

    public void setArg(Expression arg) {
        this.arg = arg;
    }


    @Override
    public void notify(DelegateTask delegateTask) {

        System.out.println("执行逻辑回归计划:" + arg.getValue(delegateTask));

        String pipe = "{\"appName\": \"testLD\", \"filePath\": \"hdfs://172.16.31.231:9000/data\",\"isSplitSample\":1," +
                "\"trainRatio\":0.6,\"evaluator\":\"MulticlassClassificationEvaluator\"," +
                "\"originalStages\": {\"Tokenizer\": {\"inputCol\": \"content\",\"outputCol\": \"words\"}," +
                "\"HashingTF\": {\"inputCol\": \"words\",\"outputCol\": \"features\"}," +
                "\"LogisticRegression\": {\"maxIter\": 10,\"regParam\": 0.001}}}";
        String url = "http://localhost:3001/LRDemo";
        JavaRequestPythonService jrps = new JavaRequestPythonService();
        jrps.requestPythonService(pipe,url);
    }
}
