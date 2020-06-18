package com.gnn.util;

import com.antfin.util.DistanceFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GNNHelper {

    public static <K> List<Map<K, List<K>>> partitionDict(Map<K, List<K>> vertices, int workers) {
        int batchSize = (vertices.size() - 1) / workers + 1;
        List<Map<K, List<K>>> partList = new ArrayList<>();
        Map<K, List<K>> part = new HashMap<>();
        int i = 0;
        for (Map.Entry<K, List<K>> entry : vertices.entrySet()) {
            K mapKey = entry.getKey();
            List<K> mapValue = entry.getValue();
            part.put(entry.getKey(), entry.getValue());
            ++i;
            if (i % batchSize == 0) {
                partList.add(part);
                part = new HashMap<>();
            }
        }
        if (!part.isEmpty()) {
            partList.add(part);
        }
        return partList;
    }

    /**
     * @return the distance between 2 time series
     */
    public static double dtw(List<Object> a, List<Object> b, int radius, DistanceFunction disFun) {
        double result = 0.0;
        if (!a.isEmpty() && !b.isEmpty()) {
            int la = a.size(), lb = b.size();
            double[][] distance = new double[la][lb];
            double[][] dp = new double[la + 1][lb + 1];
            for (int i = 0; i < la; ++i) {
                dp[i + 1][0] = Double.MAX_VALUE;
                for (int j = 0; j < lb; ++j) {
                    distance[i][j] = disFun.calcDistance(a.get(i), b.get(j));
                }
            }
            for (int j = 1; j <= lb; ++j) {
                dp[0][j] = Double.MAX_VALUE;
            }
            for (int i = 1; i <= la; ++i) {
                for (int j = 1; j <= lb; ++j) {
                    dp[i][j] = distance[i - 1][j - 1] + Math.min(
                        Math.min(dp[i - 1][j], dp[i][j - 1]),
                        dp[i - 1][j - 1] + distance[i - 1][j - 1]
                    );
                }
            }
            result = dp[la][lb];
        }
        return result;
    }

    /**
     * @param radius size of neighborhood when expanding the path
     * @param disFun The method for calculating the distance between a[i] and b[j]
     * @return the approximate distance between 2 time series with O(N) time and memory complexity
     */
    public static double fastDtw(List<Object> a, List<Object> b, int radius, DistanceFunction disFun) {
        double result = 0.0;
        if (!a.isEmpty() && !b.isEmpty()) {
            if (a.get(0) instanceof Double) {

            } else {

            }
        }
        return result;
    }
}
