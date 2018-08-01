package com.bonc.pezy.pyapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 冯刚 on 2018/6/6.
 */
public class JavaRequestPythonService {

    public String requestPythonService(String pipe,String urls){

        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(urls);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(pipe.getBytes());
            outputStream.flush();
            System.out.println(conn.getResponseMessage());
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
            return conn.getResponseMessage();

        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
