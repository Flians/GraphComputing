package com.gnn.embedding;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.graph.Graph;
import com.antfin.graph.refObj.Graph_Map_CSR;
import com.antfin.util.DegreeDistance;
import com.antfin.util.DistanceFunction;
import com.antfin.util.GraphHelper;
import com.antfin.util.OptDegreeDistance;
import com.gnn.util.GNNHelper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.apache.commons.collections.map.HashedMap;

public class Struc2vec<K, VV, EV> {

    private Graph graph;
    // Length of walk per vertex, default is 10.
    private int walkLength;
    // Number of walks per vertex, default is 100.
    private int numWalks;
    // Number of parallel workers, default is 4.
    private int workers;

    private double stayProb = 0.3;

    private boolean opt1_reduce_len;
    private boolean opt2_reduce_sim_calc;
    private boolean opt3_reduce_layers;
    private int opt3_num_layers;

    private String tempPath;
    private boolean reuse = false;

    public Struc2vec() {
        this.graph = new Graph_Map_CSR();
        this.walkLength = 10;
        this.numWalks = 100;

        this.workers = 4;

        this.opt1_reduce_len = true;
        this.opt2_reduce_sim_calc = false;
        this.opt3_reduce_layers = false;
        this.opt3_num_layers = 10;

        this.tempPath = "./temp/struc2vec/";
    }

    public Struc2vec(Graph graph, int walkLength, int numWalks, int workers, double stayProb, boolean opt1_reduce_len, boolean opt2_reduce_sim_calc, boolean opt3_reduce_layers, int opt3_num_layers, String tempPath) {
        this();
        this.graph = graph;
        this.walkLength = walkLength;
        this.numWalks = numWalks;
        this.workers = workers;
        this.stayProb = stayProb;
        this.opt1_reduce_len = opt1_reduce_len;
        this.opt2_reduce_sim_calc = opt2_reduce_sim_calc;
        this.opt3_reduce_layers = opt3_reduce_layers;
        this.opt3_num_layers = opt3_num_layers;
        this.tempPath = tempPath;
    }

    public Struc2vec(String path, int walkLength, int numWalks, int workers, double stayProb, boolean opt1_reduce_len, boolean opt2_reduce_sim_calc, boolean opt3_reduce_layers, int opt3_num_layers, String tempPath) {
        this();
        this.graph = new Graph_Map_CSR(GraphHelper.loadEdges(path), false);
        this.walkLength = walkLength;
        this.numWalks = numWalks;
        this.workers = workers;
        this.stayProb = stayProb;
        this.opt1_reduce_len = opt1_reduce_len;
        this.opt2_reduce_sim_calc = opt2_reduce_sim_calc;
        this.opt3_reduce_layers = opt3_reduce_layers;
        this.opt3_num_layers = opt3_num_layers;
        this.tempPath = tempPath;
    }

    public Struc2vec(String path) {
        this();
        this.graph = new Graph_Map_CSR(GraphHelper.loadEdges(path), false);
    }

    public void createContextGraph(int numLayers, int workers) throws IOException {
        Map<Pair<K, K>, List<Double>> pairDistance = computeStructuralDistance(numLayers, workers);
        buildWeightedLayeredGraph(pairDistance);
    }


    public Map<Pair<K, K>, List<Double>> computeStructuralDistance(int numLayers, int workers) throws IOException {
        File structDistanceFile = new File(this.tempPath + "struct_distance.kryo");
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

            File degreeListFile = new File(this.tempPath + "degree_list.kryo");
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
                    if (k == null) {
                        System.out.println(k + " is null in Struc2vec.computeStructuralDistance.degreeList.keySet().forEach");
                    }
                    vertices.put(k, new ArrayList<K>());
                });
                vertices.values().removeIf(value -> value.size() == 0);
            }
            structDistance = new HashMap<>();
            GNNHelper.partitionDict(vertices, workers).parallelStream().parallel().forEach(part -> {
                structDistance.putAll(computeDtwDist(part, degreeList, disFun));
            });
            computeFk(structDistance);
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
                orderdDegreeList = ((Map<Integer, Integer>) degreeList).entrySet().stream().collect(Collectors.toList());
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
        part.forEach((v1, neighbors) -> {
            List<List<Object>> listV1 = orderedDegreeList.get(v1);
            neighbors.forEach(v2 -> {
                List<List<Object>> listV2 = orderedDegreeList.get(v2);
                int maxLayer = Math.min(listV1.size(), listV2.size());
                List<Double> listDis = new ArrayList<>();
                for (int i = 0; i < maxLayer; ++i) {
                    listDis.add(GNNHelper.dtw(listV1.get(i), listV2.get(i), 1, disFun));
                }
                if (v1 == null || v2 == null) {
                    System.out.println(String.format("(%s, %s) is null in Struc2vec.computeDtwDist.part.forEach", v1, v2));
                }
                dtwDistance.put(new Pair<>(v1, v2), listDis);
            });
        });
        return dtwDistance;
    }

    public void computeFk(Map<Pair<K, K>, List<Double>> distance) {
        distance.entrySet().forEach(item -> {
            for (int i = 1; i < item.getValue().size(); ++i) {
                item.getValue().set(i, item.getValue().get(i) + item.getValue().get(i - 1));
            }
        });
    }

    public void buildWeightedLayeredGraph(Map<Pair<K, K>, List<Double>> pairDistance) throws IOException {
        List<Map<Pair<K, K>, Double>> layersDistance = new ArrayList<>();
        List<Map<K, List<K>>> layersAdj = new ArrayList<>();

        pairDistance.forEach((pair, distance) -> {
            for (int i = 0; i < distance.size(); ++i) {
                if (layersDistance.size() <= i) {
                    layersDistance.add(new HashMap<Pair<K, K>, Double>());
                }
                layersDistance.get(i).put(pair, distance.get(0));

                if (layersAdj.size() <= i) {
                    layersAdj.add(new HashMap<>());
                }
                if (!layersAdj.get(i).containsKey(pair.getKey())) {
                    layersAdj.get(i).put(pair.getKey(), new ArrayList<>());
                }
                if (!layersAdj.get(i).containsKey(pair.getValue())) {
                    layersAdj.get(i).put(pair.getValue(), new ArrayList<>());
                }
                layersAdj.get(i).get(pair.getKey()).add(pair.getValue());
                layersAdj.get(i).get(pair.getValue()).add(pair.getKey());
            }
        });
        GraphHelper.writeObject(layersAdj, new File(String.format("%slayers_adj.kryo", this.tempPath)));
        getTransitionProbabilityInLayer(layersDistance, layersAdj);
        getTransitionProbabilityBetweenLayer();
    }

    public void getTransitionProbabilityInLayer(List<Map<Pair<K, K>, Double>> layersDistance, List<Map<K, List<K>>> layersAdj) throws IOException {
        List<Map<K, List<Double>>> layersAlias = new ArrayList<>(layersAdj.size());
        List<Map<K, List<Double>>> layersAccept = new ArrayList<>(layersAdj.size());
        for (int layer = 0; layer < layersAdj.size(); ++layer) {
            Map<Pair<K, K>, Double> layerDistance = layersDistance.get(layer);
            Map<K, List<Double>> node_alias_dict = new HashMap<>();
            Map<K, List<Double>> node_accept_dict = new HashMap<>();
            Map<K, List<Double>> norm_weights = new HashMap<>();
            layersAdj.get(layer).forEach((v, neighbors) -> {
                List<Double> edgeWeight = new ArrayList<>();
                double sumWeight = 0.0;
                for (K n : neighbors) {
                    double wd;
                    if (layerDistance.containsKey(new Pair<>(v, n))) {
                        wd = layerDistance.get(new Pair<>(v, n));
                    } else {
                        wd = layerDistance.get(new Pair<>(n, v));
                    }
                    wd = Math.exp(-wd);
                    edgeWeight.add(wd);
                    sumWeight += wd;
                }
                for (int j = 0; j < edgeWeight.size(); ++j) {
                    edgeWeight.set(j, edgeWeight.get(j) / sumWeight);
                }
                norm_weights.put(v, edgeWeight);
                GNNHelper.createAliasTable(edgeWeight, v, node_alias_dict, node_accept_dict);
            });
            GraphHelper.writeObject(norm_weights, new File(String.format("%snorm_weights_distance_layer-%d.kryo", this.tempPath, layer)));
            layersAlias.add(node_alias_dict);
            layersAccept.add(node_accept_dict);
        }
        GraphHelper.writeObject(layersAlias, new File(String.format("%slayers_alias.kryo", this.tempPath)));
        GraphHelper.writeObject(layersAccept, new File(String.format("%slayers_accept.kryo", this.tempPath)));
    }

    public void getTransitionProbabilityBetweenLayer() throws IOException {
        List<Double> averageWeight = new ArrayList<>();
        List<Map<K, Integer>> gamma = new ArrayList<>();
        int layer = 0;
        while (true) {
            File layerK = new File(String.format("%snorm_weights_distance_layer-%d.kryo", tempPath, layer));
            if (layerK.exists()) {
                double sumWeights = 0.0;
                double sumEdges = 0.0;
                Map<K, List<Double>> norm_weights = (Map<K, List<Double>>) GraphHelper.loadObject(layerK);
                for (Entry<K, List<Double>> entry : norm_weights.entrySet()) {
                    K v = entry.getKey();
                    List<Double> weightList = entry.getValue();
                    sumWeights += entry.getValue().stream().mapToDouble(Double::doubleValue).sum();
                    sumEdges += entry.getValue().size();
                }
                averageWeight.add(sumWeights / sumEdges);
                Map<K, Integer> layerGamma = new HashMap<>();
                double avg = averageWeight.get(layer);
                norm_weights.forEach((v, weightList) -> {
                    layerGamma.put(v, (int) weightList.stream().filter(w -> w > avg).count());
                });
                gamma.add(layerGamma);
                ++layer;
            } else {
                break;
            }
        }
        GraphHelper.writeObject(averageWeight, new File(String.format("%saverage_weight.kryo", this.tempPath)));
        GraphHelper.writeObject(gamma, new File(String.format("%sgamma.kryo", this.tempPath)));
    }

    public List<List<K>> struc2vecWalk(int numWalks, int walkLength, double stayProb, int workers) throws IOException {
        List<Map<K, List<Double>>> layersAlias = (List<Map<K, List<Double>>>) GraphHelper.loadObject(new File(String.format("%slayers_alias.kryo", this.tempPath)));
        List<Map<K, List<Double>>> layersAccept = (List<Map<K, List<Double>>>) GraphHelper.loadObject(new File(String.format("%slayers_accept.kryo", this.tempPath)));
        List<Map<K, List<K>>> layersAdj = (List<Map<K, List<K>>>) GraphHelper.loadObject(new File(String.format("%slayers_adj.kryo", this.tempPath)));
        List<Map<K, Integer>> gamma = (List<Map<K, Integer>>) GraphHelper.loadObject(new File(String.format("%sgamma.kryo", this.tempPath)));

        int initialLayer = 0;
        List<List<K>> walks = new ArrayList();
        GNNHelper.partitionNumber(numWalks, workers).parallelStream().parallel().forEach(part -> {
            walks.addAll(GNNHelper.simulateWalks(this.graph.getVertexList(), numWalks, walkLength, stayProb, initialLayer, layersAlias, layersAccept,layersAdj,gamma));
        });
        return walks;
    }

    public void train(int embed_size, int window_size, int workers, int iterator) {

    }

}
