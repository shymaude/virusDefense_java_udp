package com.security.defense.util;

public class NumberParsingUtils {

    public static Integer parseInteger(String valueStr) {
        Integer value = null;
        try {
            value = Integer.parseInt(valueStr);
        } catch(NumberFormatException e) {}
        return value;
    }

    public static Double parseDouble(String valueStr) {
        Double value = null;
        try {
            value = Double.parseDouble(valueStr);
        } catch(NumberFormatException e) {}
        return value;
    }
}