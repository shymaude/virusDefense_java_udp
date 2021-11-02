package com.security.defense.defense;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.defense.Message;
import com.security.defense.Node;
import com.security.defense.NodeServer;
import com.security.defense.util.SampleUtils;
import com.security.defense.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FormGroups implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FormGroups.class);

    @Autowired
    private Node node;

    @Autowired
    private NodeServer nodeServer;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run() {
        while(true) {
            Set<Integer> usedNodes = new HashSet<>();
            int nodeNumber = node.getNodeNumber();
            for(int i = 0; i < nodeNumber; i++) {
                Set<String> group = new HashSet<>();
                for (int j = 0; j < node.getGroupSize(); j++) {
                    Integer sampleNode;
                    do {
                        sampleNode = SampleUtils.sampleNat(nodeNumber);
                    } while(group.contains(sampleNode) || usedNodes.contains(sampleNode));
                    group.add(node.getNodes().get(sampleNode));
                    usedNodes.add(sampleNode);
                    if(usedNodes.size() == nodeNumber) {
                        usedNodes = new HashSet<>();
                    }
                }
                try {
                    nodeServer.send(node.getNodes().get(i), new Message("group", objectMapper.writeValueAsString(group)));
                } catch (JsonProcessingException e) {
                    System.err.println("Error converting group set to json: " + e.getMessage() + ", group=" + group.stream().collect(Collectors.joining(", ")));
                }
            }
            ThreadUtils.sleep(SampleUtils.sampleExpMillis(node.getFormGroupRate()));
        }
    }
}