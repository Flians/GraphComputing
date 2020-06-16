package com.antfin.gnn.embedding;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.graph.Graph;
import com.antfin.graph.refObj.Graph_Map_CSR;
import com.antfin.util.DegreeDistance;
import com.antfin.util.DistanceFunction;
import com.antfin.util.GraphHelper;
import com.antfin.util.OptDegreeDistance;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.apache.commons.collections.map.HashedMap;

public class Struc2vec<K, VV, EV> {

    private Graph graph;
    // Graph is (un)directed, default is undirected.
    private boolean directed;
    // Length of walk per vertex, default is 10.
    private int walk_length;
    // Number of walks per vertex, default is 100.
    private int num_walks;
    // Number of parallel workers, default is 4.
    private int workers;
    private int verbose = 0;
    private double stay_prob = 0.3;

    private boolean opt1_reduce_len;
    private boolean opt2_reduce_sim_calc;
    private boolean opt3_reduce_layers;
    private int opt3_num_layers;

    private String tempPath;
    private boolean reuse = false;

    public Struc2vec() {
        this.graph = new Graph_Map_CSR();
        this.walk_length = 10;
        this.num_walks = 100;

        this.workers = 4;

        this.opt1_reduce_len = true;
        this.opt2_reduce_sim_calc = true;
        this.opt3_reduce_layers = true;
        this.opt3_num_layers = 10;

        this.tempPath = "./temp/struc2vec/";

    }

    public void createContextGraph(int numLayers, int workers, int verbose) {

    }


    public Map<Pair<K, K>, List<Double>> computeStructuralDistance(int numLayers, int workers, int verbose) throws IOException {
        File structDistanceFile = new File(tempPath + "struct_distance.kryo");
        Map<Pair<K, K>, List<Double>> structDistance;
        if (structDistanceFile.exists()) {
            structDistance = (Map<Pair<K, K>, List<Double>>) GraphHelper.loadObject(structDistanceFile);
        } else {
            DistanceFunction disFun;
            if (this.opt1_reduce_len) {
                disFun = new OptDegreeDistance();
            } else {
                disFun = new DegreeDistance();
            }

            File degreeListFile = new File(tempPath + "degree_list.kryo");
            Map<K, List<List<Object>>> degreeList;
            if (degreeListFile.exists()) {
                degreeList = (Map<K, List<List<Object>>>) GraphHelper.loadObject(degreeListFile);
            } else {
                degreeList = computeOrderedDegreeList(numLayers);
                GraphHelper.writeObject(degreeList, degreeListFile);
            }

            Map<K, List<K>> vertices = new HashMap<>();
            if (this.opt2_reduce_sim_calc) {

            } else {
                degreeList.keySet().forEach(k -> {
                    vertices.keySet().forEach(item -> {
                        vertices.get(item).add(k);
                    });
                    vertices.put(k, new ArrayList<K>());
                });
                vertices.values().removeIf(value -> value.size() == 0);
            }
            structDistance = new HashMap<>();
            GraphHelper.partitionDict(vertices, workers).stream().parallel().forEach(part -> {
                structDistance.putAll(computeDtwDist(part, degreeList, disFun));
            });
            GraphHelper.writeObject(structDistance, structDistanceFile);
        }
        return structDistance;
    }

    public List<List<Object>> getOrderedDegreeListOfNode(K kid, int numLayers) {
        List<List<Object>> orderedDegreeSeqList = new ArrayList<>();
        Queue<K> bfs = new LinkedList<>();
        Map<K, Boolean> visited = new HashMap<>();
        bfs.add(kid);
        visited.put(kid, true);

        while (!bfs.isEmpty() && orderedDegreeSeqList.size() <= numLayers) {
            int nums = bfs.size();
            Object degreeList;
            if (this.opt1_reduce_len) {
                // (degree, number), ordered
                degreeList = new TreeMap<Integer, Integer>();
            } else {
                degreeList = new ArrayList<Integer>();
            }
            while (nums > 0) {
                K cur = bfs.poll();
                List<Edge<K, EV>> outEdges = (List<Edge<K, EV>>) this.graph.getEdge(cur);
                // calculate the degree
                if (this.opt1_reduce_len) {
                    if (((Map) degreeList).containsKey(outEdges.size())) {
                        ((Map) degreeList).put(outEdges.size(), ((Map<Integer, Integer>) degreeList).get(outEdges.size()) + 1);
                    } else {
                        ((Map) degreeList).put(outEdges.size(), 1);
                    }
                } else {
                    ((List) degreeList).add(outEdges.size());
                }
                outEdges.forEach(e -> {
                    if (!visited.containsKey(e.getTargetId())) {
                        visited.put(e.getTargetId(), true);
                        bfs.add(e.getTargetId());
                    }
                });
                --nums;
            }
            List<Object> orderdDegreeList;
            if (this.opt1_reduce_len) {
                orderdDegreeList = (List)((Map<Integer, Integer>) degreeList).entrySet();
                // orderdDegreeList = ((Map<Integer, Integer>) degreeList).entrySet().stream().map(ent -> new Pair<Integer, Integer>(ent.getKey(), ent.getValue())).collect(Collectors.toList());
            } else {
                Collections.sort((List<Integer>) degreeList);
                orderdDegreeList = (List<Object>) degreeList;
            }
            orderedDegreeSeqList.add(orderdDegreeList);
        }
        return orderedDegreeSeqList;
    }

    public Map<K, List<List<Object>>> computeOrderedDegreeList(int numLayers) {
        return (Map<K, List<List<Object>>>) this.graph.getVertexList().stream().collect(
            Collectors.toMap(k -> ((Vertex) k).getId(), k -> getOrderedDegreeListOfNode((K) ((Vertex) k).getId(), numLayers))
        );
    }

    public Map<Pair<K, K>, List<Double>> computeDtwDist(Map<K, List<K>> part, Map<K, List<List<Object>>> orderedDegreeList, DistanceFunction disFun) {
        Map<Pair<K, K>, List<Double>> dtwDistance = new HashedMap();
        part.entrySet().forEach(ent -> {
            List<List<Object>> listV1 = orderedDegreeList.get(ent.getKey());
            ent.getValue().forEach(v2 -> {
                List<List<Object>> listV2 = orderedDegreeList.get(v2);
                int maxLayer = Math.min(listV1.size(), listV2.size());
                List<Double> listDis = new ArrayList<>();
                for (int i = 0; i < maxLayer; ++i) {
                    listDis.add(GraphHelper.dtw(listV1.get(i), listV2.get(i), 1, disFun));
                }
                dtwDistance.put(new Pair<>(ent.getKey(), v2), listDis);
            });
        });
        return dtwDistance;
    }

    public void computeFk(Map<Pair<K, K>, List<Double>> distance) {
        distance.entrySet().forEach(item -> {
            for (int i=1; i<item.getValue().size(); ++i) {
                item.getValue().set(i, item.getValue().get(i) + item.getValue().get(i-1));
            }
        });
    }
}
