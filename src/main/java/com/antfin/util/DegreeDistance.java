package com.antfin.util;


public class DegreeDistance implements DistanceFunction {

    public DegreeDistance() {

    }

    public double calcDistance(Object a, Object b) {
        if (!(a instanceof Double) || !(b instanceof Double)) {
            System.err.println("Object " + a + ", " + b + " are not Double!");
        }
        double ep = 0.5;
        double ma = Math.max((double) a, (double) b) + ep;
        double mi = Math.min((double) a, (double) b) + ep;
        return (ma / mi) - 1;
    }
}