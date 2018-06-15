package com.deepthoughtdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@RestController
public class Test {
    @Autowired
    private RestTemplate restTemplate;
    @RequestMapping("/java-user")
    public String JavaUser() {
        return "{'username': 'java', 'password': 'java'}"  ;
    }
    @RequestMapping("/python-user")
    public String PythonUser() {
        return restTemplate.getForEntity("http://py-sidecar/getUser", String.class).getBody();
    }

}
