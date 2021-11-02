package com.security.defense;

import org.apache.commons.math3.distribution.TDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class NodeController {

    private static final Logger log = LoggerFactory.getLogger(NodeController.class);

    @Autowired
    private Node node;

    @Autowired
    private NodeRestTemplate nodeRestTemplate;

    @PostMapping("/nodes")
    public List<String> getNodes() {
        return node.getNodes();
    }

    @PostMapping("/infect")
    public boolean infect() {
        boolean infected = false;
        if(!node.isBlocked()) {
            infected = node.isInfected() || !node.isFiltering();
            node.setInfected(infected);
        }
        return infected;
    }

    @PostMapping("/group")
    public void group(@RequestBody Set<String> group) {
        node.setGroup(group);
    }

    @PostMapping("/alert")
    public void alert() {
        node.incAlerts();
    }

    @PostMapping("/infected")
    @ResponseBody
    public boolean isInfected() {
        return node.isInfected();
    }

    @PostMapping("/numberOfInfected")
    @ResponseBody
    public int getNumberOfInfected() {
        int numberOfInfected = 0;
        for(String nodeAddress : node.getNodes()) {
            if(nodeRestTemplate.post(nodeAddress ,"infected", Boolean.class, log)) {
                numberOfInfected++;
            }
        }
        return numberOfInfected;
    }

    @PostMapping("/blocked")
    @ResponseBody
    public boolean isBlocked() {
        return node.isBlocked();
    }

    @PostMapping("/blockAndReset")
    public void blockAndReset() {
        node.setBlocked(true);
        reset();
    }

    @PostMapping("/unblock")
    public void unblock() {
        node.setBlocked(false);
    }

    @PostMapping("/someBlocked")
    @ResponseBody
    public boolean someBlocked() {
        boolean someBlocked = false;
        for(String nodeAddress : node.getNodes()) {
            if(!node.getLocalNode().equals(nodeAddress)) {
                if(nodeRestTemplate.post(nodeAddress, "blocked", Boolean.class, log)) {
                    someBlocked = true;
                    break;
                }
            }
        }
        return someBlocked;
    }

    @PostMapping("/start")
    public void start() {
        node.setBlocked(true);
        reset();
        nodeRestTemplate.postToAllNodes(node, "blockAndReset", log);
        while(getNumberOfInfected() > 0);
        nodeRestTemplate.postToAllNodesAsync(node, "unblock", log);
        while(someBlocked());
        node.setBlocked(false);
        node.setInfected(true);
    }

    @PostMapping("/reset")
    public void reset() {
        node.setInfected(false);
        node.setDetected(false);
        node.setFiltering(false);
        node.setAlert(0);
        node.setAlerts(0);
        node.setCount(0);
    }

    @PostMapping("/studentCumulativeValue")
    @ResponseBody
    public double getStudentCumulativeValue(
            @RequestParam("degreesOfFreedom") int degreesOfFreedom,
            @RequestParam("alpha") double alpha
    ) {
        TDistribution tDistribution = new TDistribution(degreesOfFreedom);
        return tDistribution.cumulativeProbability(alpha);
    }
}