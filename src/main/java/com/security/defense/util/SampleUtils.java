package com.security.defense.util;

import com.security.defense.Node;

public class SampleUtils {

    public static double sampleExp(double mean) {
        return - mean * Math.log(Math.random());
    }

    public static long sampleExpMillis(double mean) {
        return Math.round(sampleExp(mean) * Node.SECONDS_TO_MILLIS_FACTOR);
    }

    public static int sampleNat(int upperBound) { return (int) Math.round(Math.random() * (upperBound - 1)); }

    public static boolean sampleBernulli(double prob) { return Math.random() < prob; }
}