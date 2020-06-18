package com.gnn.util;

import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.util.DistanceFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

    public static List<Integer> partitionNumber(int numWalks, int workers) {
        List<Integer> res = new ArrayList<>();
        int l1 = numWalks / workers;
        int l2 = numWalks % workers;
        while ((workers--) > 0 && l1 != 0) {
            res.add(l1);
        }
        if (l2 != 0) {
            res.add(l2);
        }
        return res;
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

    public static <K> void createAliasTable(List<Double> edgeWeight, K v, Map<K, List<Double>> node_alias_dict, Map<K, List<Double>> node_accept_dict) {
        List<Double> accept = new ArrayList<>(edgeWeight.size());
        List<Double> alias = new ArrayList<>(edgeWeight.size());
        Stack<Integer> small = new Stack<>();
        Stack<Integer> large = new Stack<>();
        List<Double> edgeWeight_ = new ArrayList<>();
        for (int i = 0; i < edgeWeight.size(); ++i) {
            accept.add(0.0);
            alias.add(0.0);
            edgeWeight_.add(edgeWeight.get(i) * edgeWeight.size());
            if (edgeWeight_.get(i) < 1) {
                small.push(i);
            } else {
                large.push(i);
            }
        }

        while (small.size() > 0 && large.size() > 0) {
            int index_small = small.pop();
            int index_large = large.pop();

            accept.set(index_small, edgeWeight_.get(index_small));
            alias.set(index_small, (double) index_large);
            edgeWeight_.set(index_large, edgeWeight_.get(index_large) - (1 - edgeWeight_.get(index_small)));
            if (edgeWeight_.get(index_large) < 1.0) {
                small.push(index_large);
            } else {
                large.push(index_large);
            }
        }

        while (large.size() > 0) {
            accept.set(large.pop(), 1.0);
        }
        while (small.size() > 0) {
            accept.set(small.pop(), 1.0);
        }

        node_alias_dict.put(v, alias);
        node_accept_dict.put(v, accept);
    }

    public static <K> List<List<K>> simulateWalks(List<Vertex> vertices, int numWalks, int walkLength, double stayProb, int initialLayer,
                                         List<Map<K, List<Double>>> layersAlias, List<Map<K, List<Double>>> layersAccept, List<Map<K, List<K>>> layersAdj, List<Map<K, Integer>> gamma) {
        List<List<K>> walks = new ArrayList();
        while ((numWalks--) > 0) {
            Collections.shuffle(vertices);
            vertices.forEach(v -> {
                List<K> walk = new ArrayList();
                walk.add((K) v.getId());
                int layer = initialLayer;
                while (walk.size() < walkLength) {
                    double r = Math.random();
                    double rx = Math.random();
                    // same layer
                    if (r < stayProb) {
                        layersAdj.get(layer).get(v.getId());
                        int vid = (int) (Math.random()*layersAccept.get(layer).get(v.getId()).size());
                        if (rx >= layersAccept.get(layer).get(v.getId()).get(vid)) {
                            vid = layersAlias.get(layer).get(v.getId()).get(vid).intValue();
                        }
                        K neighbor = layersAdj.get(layer).get(v.getId()).get(vid);
                        walk.add(neighbor);
                    } else {
                        // different layer
                        double w = Math.log(gamma.get(layer).get(v.getId()) + Math.E);
                        double probUp = w/(w+1);
                        if (rx > probUp && layer > initialLayer) {
                            layer = layer - 1;
                        } else {
                            if (layer + 1 < layersAdj.size() && layersAdj.get(layer+1).containsKey(v.getId())) {
                                ++layer;
                            }
                        }
                    }
                }
                walks.add(walk);
            });
        }
        return walks;
    }
}