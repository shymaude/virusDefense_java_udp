package com.security.defense.defense;

import com.security.defense.Node;
import com.security.defense.util.SampleUtils;
import com.security.defense.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Filter implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Filter.class);

    @Autowired
    private Node node;

    @Override
    public void run() {
        while(true) {
            ThreadUtils.sleep(node.getDtMillis());
            boolean detectedAndZeroCount = node.isDetected() && node.getCount() == 0;
            node.setCount(detectedAndZeroCount ? SampleUtils.sampleNat(node.getAlpha()) : node.getAlert());
            node.setAlert(
                    Math.min(
                            node.getAlpha(),
                            detectedAndZeroCount ? node.getAlert() + node.getSeverity() * (node.getAlerts() + 1) : node.getAlert() + node.getSeverity() * node.getAlerts()
                    )
            );
            node.setAlerts(0);
            if(node.getAlert() == node.getAlpha()) {
                node.setFiltering(true);
            } else if(node.getAlert() == 0) {
                node.setFiltering(false);
            }
            node.decAlert();
            node.decCount();
        }
    }
}