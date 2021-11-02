package com.security.defense.defense;

import com.security.defense.Message;
import com.security.defense.Node;
import com.security.defense.NodeServer;
import com.security.defense.util.SampleUtils;
import com.security.defense.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.SetUtils;

@Component
public class Alert implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Alert.class);

    @Autowired
    private Node node;

    @Autowired
    private NodeServer nodeServer;

    @Override
    public void run() {
        while(true) {
            ThreadUtils.sleep(SampleUtils.sampleExpMillis(node.getAlertGroupRate()));
            if(node.isDetected() && !SetUtils.isEmpty(node.getGroup())) {
                node.getGroup().forEach(groupNode -> nodeServer.send(groupNode ,new Message("alert")));
            }
        }
    }
}