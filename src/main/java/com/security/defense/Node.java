package com.security.defense;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Data
@NoArgsConstructor
public class Node {

    public static final int SECONDS_TO_MILLIS_FACTOR = 1000;

    private boolean infected = false;
    private boolean detected = false;
    private boolean filtering = false;
    private boolean blocked = true;

    private String localNode;
    private int localNodeIndex;
    private Integer nodeNumber;
    private List<String> nodes = new ArrayList<>();

    private Set<String> group;
    private Integer groupSize;

    private Double dt;
    private Integer r;
    private Integer severity;
    private Double fpRate;
    private Double detectionProb;
    private Double propagateRate;
    private Double formGroupRate;
    private Double alertGroupRate;
    private Double detectRate;
    private Double alertRate;

    private Double tStudentAlpha;

    private int alpha;
    private int alert = 0;
    private int alerts = 0;
    private int count = 0;

    public long getDtMillis() { return Math.round(dt * SECONDS_TO_MILLIS_FACTOR); }

    public void incAlerts() { alerts++; }
    public void decAlert() { if(alert > 0) alert--; }
    public void decCount() { if(count > 0) count--; }
}