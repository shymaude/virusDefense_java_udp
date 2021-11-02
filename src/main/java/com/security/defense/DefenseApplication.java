package com.security.defense;

import com.security.defense.defense.*;
import com.security.defense.util.NumberParsingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.util.FieldUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
public class DefenseApplication {

    private static final Logger log = LoggerFactory.getLogger(DefenseApplication.class);

    private static final List<String> MANDATORY_PROPS = Arrays.asList(
            "nodeNumber", "groupSize",
            "dt", "r", "severity",
            "fpRate", "detectionProb",
            "propagateRate", "formGroupRate", "alertGroupRate", "detectRate", "alertRate",
            "tStudentAlpha"
    );

    @Value("${server.port}")
    private String port;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private Node node;

    @Autowired
    private NodeServer nodeServer;

    @Autowired
    private FormGroups formGroups;

    @Autowired
    private Detect detect;

    @Autowired
    private Alert alert;

    @Autowired
    private Filter filter;

    @Autowired
    private Infection infection;

    private static int localNodeIndex;
    private static List<String> nodes = new ArrayList<>();

    public static void main(String[] args) {
        if(args.length < 2) {
            log.error("Pass: the index of the local node in the list of node ip addresses and the list of node ip addresses");
            exit();
        }
        localNodeIndex = NumberParsingUtils.parseInteger(args[0]);
        checkArg(localNodeIndex, "Can't parse local node index!");
        for(int i = 1; i < args.length; i++) {
            String nodeAddress = args[i];
            checkArg(nodeAddress, "Can't parse node " + (i - 1) + "!");
            nodes.add(nodeAddress);
        }
        SpringApplication.run(DefenseApplication.class, args);
    }

    @PostConstruct
    private void start() {
        readProperties();
        initNodeParams();
        nodeServer.setPort(Integer.parseInt(port));
        taskExecutor.execute(() -> nodeServer.run());
        if(localNodeIndex == 0) {
            taskExecutor.execute(() -> formGroups.run());
        }
        taskExecutor.execute(() -> detect.run());
        taskExecutor.execute(() -> alert.run());
        taskExecutor.execute(() -> filter.run());
        taskExecutor.execute(() -> infection.run());
        log.info("Defense with UDP messaging started!!!");
    }

    private void initNodeParams() {
        node.setNodes(nodes);
        node.setLocalNodeIndex(localNodeIndex);
        node.setLocalNode(node.getNodes().get(localNodeIndex));
        node.setAlpha(node.getR() * node.getSeverity());
        log.info("nodeNumber=" + node.getNodeNumber());
        log.info("groupSize=" + node.getGroupSize());
        log.info("nodes=" + node.getNodes().stream().collect(Collectors.joining(", ")));
        log.info("localNodeIndex=" + node.getLocalNodeIndex());
        log.info("localNode=" + node.getLocalNode());
        log.info("alpha=" + node.getAlpha());
    }

    private void readProperties() {
        Properties props = null;
        try {
            props = PropertiesLoaderUtils.loadAllProperties("application.properties");
        } catch (IOException e) {
            log.error(e.getMessage());
            exit();
        }
        if(props == null) {
            log.info("Can't read properties");
            exit();
        }
        for(String propName : MANDATORY_PROPS) {
            Field field = FieldUtils.getField(Node.class, propName);
            if(Double.class.equals(field.getType())) {
                Double value = NumberParsingUtils.parseDouble(props.getProperty(propName));
                if(value == null) {
                    log.info("Can't read " + propName);
                    exit();
                }
                FieldUtils.setProtectedFieldValue(propName, node, value);
                log.info(propName + "=" + value);
            } else if(Integer.class.equals(field.getType())) {
                Integer value = NumberParsingUtils.parseInteger(props.getProperty(propName));
                if(value == null) {
                    log.info("Can't read " + propName);
                    exit();
                }
                FieldUtils.setProtectedFieldValue(propName, node, value);
                log.info(propName + "=" + value);
            }
        }
    }

    private static void checkArg(Object value, String error) {
        if(value == null) {
            log.error(error);
            exit();
        }
    }

    private static void exit() {
        System.exit(0);
    }
}