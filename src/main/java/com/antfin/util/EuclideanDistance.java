package com.antfin.util;


import java.util.List;

public class EuclideanDistance implements DistanceFunction {

    public EuclideanDistance() {

    }

    public double calcDistance(List<Double> vector1, List<Double> vector2) {
       if (vector1.size() != vector2.size()) {
          throw new InternalError("ERROR:  cannot calculate the distance "
              + "between vectors of different sizes.");
       }

        double sqSum = 0.0;
       for (int x = 0; x < vector1.size(); x++) {
          sqSum += Math.pow(vector1.get(x) - vector2.get(x), 2.0);
       }

        return Math.sqrt(sqSum);
    }  // end class euclideanDist(..)

    @Override
    public double calcDistance(Object a, Object b) {
        return this.calcDistance((List<Double>) a, (List<Double>) b);
    }

}