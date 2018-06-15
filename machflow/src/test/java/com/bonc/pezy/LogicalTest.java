package com.bonc.pezy;

/*import org.shirdrn.workflow.activiti.AbstractTest;*/

import com.bonc.pezy.pyapi.JavaRequestPythonService;

/**
 * Created by 冯刚 on 2018/6/8.
 */
public class LogicalTest{
    public static void main(String[] args){

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
