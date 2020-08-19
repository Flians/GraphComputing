package com.antfin.util;


import java.util.List;

public class ManhattanDistance implements DistanceFunction {

    public ManhattanDistance() {

    }

    public double calcDistance(List<Double> vector1, List<Double> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new InternalError("ERROR:  cannot calculate the distance "
                + "between vectors of different sizes.");
        }

        double diffSum = 0.0;
        for (int x = 0; x < vector1.size(); x++) {
            diffSum += Math.abs(vector1.get(x) - vector2.get(x));
        }

        return diffSum;
    }

    @Override
    public double calcDistance(Object a, Object b) {
        return this.calcDistance((List<Double>)a, (List<Double>) b);
    }

}