package com.security.defense;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Component
public class NodeRestTemplate {

    @Value("${server.port}")
    private String port;

    private RestTemplate restTemplate = new RestTemplate();

    public void postToAllNodes(Node node, String cmd, Logger log) {
        node.getNodes().forEach(nodeAddress -> {
            if(!node.getLocalNode().equals(nodeAddress)) {
                post(nodeAddress, cmd, log);
            }
        });
    }

    public void postToAllNodesAsync(Node node, String cmd, Logger log) {
        node.getNodes().forEach(nodeAddress -> {
            if(!node.getLocalNode().equals(nodeAddress)) {
                new Thread(() -> { post(nodeAddress, cmd, log); }).start();
            }
        });
    }

    public void post(String node, String cmd, Logger log) {
        try {
            restTemplate.postForObject(getUrl(node, cmd), new HttpEntity(null, getHttpHeaders()), String.class);
        } catch(Exception e) {
            log.error("RestTemplate error: " + e.getMessage());
        }
    }

    public void post(String node, String cmd, Set<String> group, Logger log) {
        try {
            restTemplate.postForObject(getUrl(node, cmd), new HttpEntity(group, getHttpHeaders()), String.class);
        } catch(Exception e) {
            log.error("RestTemplate error: " + e.getMessage());
        }
    }

    public <T> T post(String node, String cmd, Class T, Logger log) {
        T result = null;
        try {
            Object x = restTemplate.postForObject(getUrl(node, cmd), new HttpEntity(null, getHttpHeaders()), T);
            result = (T) x;
        } catch(Exception e) {
            log.error("RestTemplate error: " + e.getMessage());
        }
        return result;
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public String getUrl(String node, String cmd) {
        return "http://" + node + ":" + port + "/" + cmd;
    }
}