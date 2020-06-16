package com.antfin.util;


public class DistanceFunctionFactory {

    public static DistanceFunction OPT_DEGREE_DIS_FUN = new OptDegreeDistance();
    public static DistanceFunction DEGREE_DIS_FUN = new DegreeDistance();

    public static DistanceFunction getDistFnByName(String disFunName) {
        if (disFunName.equals("OptDegreeDistance")) {
            return OPT_DEGREE_DIS_FUN;
        } else if (disFunName.equals("DegreeDistance")) {
            return DEGREE_DIS_FUN;
        } else {
            throw new IllegalArgumentException("There is no DistanceFunction for the name " + disFunName);
        }
    }
}