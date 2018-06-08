package com.bonc.pezy.pyapi;

/**
 * Created by 冯刚 on 2018/6/6.
 */
public class LogicalTest {
    public static void main(String[] args){

        String pipe = "{\"appName\": \"testLD\", \"filePath\": \"hdfs://172.16.31.231:9000/data\",\"isSplitSample\":1," +
                "\"trainRatio\":0.6,\"evaluator\":\"MulticlassClassificationEvaluator\"," +
                "\"originalStages\": {\"Tokenizer\": {\"inputCol\": \"content\",\"outputCol\": \"words\"}," +
                "\"HashingTF\": {\"inputCol\": \"words\",\"outputCol\": \"features\"}," +
                "\"LogisticRegression\": {\"maxIter\": 10,\"regParam\": 0.001}}}";
        String url = "http://localhost:3001/LRDemo";
        JavaRequestPythonService jrps = new JavaRequestPythonService();
        jrps.requestPythonService(pipe,url);
        /*URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL("http://localhost:3001/LRDemo");
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(pipe.getBytes());
            outputStream.flush();
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                        + conn.getResponseCode());
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            System.out.println("Output from Server:  \n");
            while ((output = responseBuffer.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }
}
