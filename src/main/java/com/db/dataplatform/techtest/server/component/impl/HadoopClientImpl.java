package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.component.HadoopClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
@Slf4j
public class HadoopClientImpl implements HadoopClient {
    private final RestTemplate restTemplate;
    private final String HADOOP_URL = "http://localhost:8090/hadoopserver/pushbigdata";
    @Override
    public Boolean pushData(String data) {
        restTemplate.postForEntity(HADOOP_URL, data, HttpStatus.class);
        return true;
    }
}
