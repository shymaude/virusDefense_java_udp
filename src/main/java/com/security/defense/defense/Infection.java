package com.security.defense.defense;

import com.security.defense.Node;
import com.security.defense.NodeRestTemplate;
import com.security.defense.util.SampleUtils;
import com.security.defense.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Infection implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Infection.class);

    @Autowired
    private Node node;

    @Autowired
    private NodeRestTemplate nodeRestTemplate;

    @Override
    public void run() {
        while(true) {
            ThreadUtils.sleep(SampleUtils.sampleExpMillis(node.getPropagateRate()));
            if(node.isInfected() && !node.isFiltering() && !node.isBlocked()) {
                int nodeToInfect = (node.getLocalNodeIndex() + 1 + SampleUtils.sampleNat(node.getNodeNumber() - 1)) % node.getNodeNumber();
                nodeRestTemplate.post(node.getNodes().get(nodeToInfect) ,"infect", log);
            }
        }
    }
}