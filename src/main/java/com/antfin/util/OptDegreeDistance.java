/*
 * Arrays.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package com.antfin.util;


import java.util.Map;
import javafx.util.Pair;

public class OptDegreeDistance implements DistanceFunction {

    public OptDegreeDistance() {
    }

    public double calcDistance(Object a, Object b) {
        if (!(a instanceof Map.Entry) || !(b instanceof Map.Entry)) {
            System.err.println("Object " + a + ", " + b + " are not Map.Entry!");
        }
        double ep = 0.5;
        double ma = Math.max(((Map.Entry<Integer, Integer>) a).getKey(), ((Map.Entry<Integer, Integer>) b).getKey()) + ep;
        double mi = Math.min(((Map.Entry<Integer, Integer>) a).getKey(), ((Map.Entry<Integer, Integer>) b).getKey()) + ep;
        return ((ma / mi) - 1) * Math.max(((Map.Entry<Integer, Integer>) a).getValue(), ((Map.Entry<Integer, Integer>) b).getValue());
    }

}