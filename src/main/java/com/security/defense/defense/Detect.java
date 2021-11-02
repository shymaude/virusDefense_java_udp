package com.security.defense.defense;

import com.security.defense.Node;
import com.security.defense.util.SampleUtils;
import com.security.defense.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Detect implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Detect.class);

    @Autowired
    private Node node;

    @Override
    public void run() {
        while(true) {
            ThreadUtils.sleep(SampleUtils.sampleExpMillis(node.getDetectRate()));
            node.setDetected(node.isInfected() ? SampleUtils.sampleBernulli(node.getDetectionProb()) : SampleUtils.sampleBernulli(node.getFpRate()));
        }
    }
}