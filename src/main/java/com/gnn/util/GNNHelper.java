package com.gnn.util;

import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.util.DistanceFunction;
import com.antfin.util.GraphHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.swing.JFrame;
import jsat.SimpleDataSet;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.datatransform.visualization.TSNE;
import jsat.linear.DenseMatrix;
import jsat.linear.Matrix;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Attribute;
import weka.core.Debug.Random;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class GNNHelper {

    public static <K> List<Map<K, List<K>>> partitionDict(Map<K, List<K>> vertices, int workers) {
        int batchSize = (vertices.size() - 1) / workers + 1;
        List<Map<K, List<K>>> partList = new ArrayList<>();
        Map<K, List<K>> part = new HashMap<>();
        int i = 0;
        for (Map.Entry<K, List<K>> entry : vertices.entrySet()) {
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

    public static int verifyDegrees(Map<Integer, Map<String, Object>> degrees, int degreesV, int degreesA, int degreesB) {
        int res = 0;
        if (degreesB == -1) {
            res = degreesA;
        } else if (degreesA == -1) {
            res = degreesB;
        } else if (Math.abs(degreesV - degreesB) < Math.abs(degreesV - degreesA)) {
            res = degreesB;
        } else {
            res = degreesA;
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
                        int vid = (int) (Math.random() * layersAccept.get(layer).get(v.getId()).size());
                        if (rx >= layersAccept.get(layer).get(v.getId()).get(vid)) {
                            vid = layersAlias.get(layer).get(v.getId()).get(vid).intValue();
                        }
                        K neighbor = layersAdj.get(layer).get(v.getId()).get(vid);
                        walk.add(neighbor);
                    } else {
                        // different layer
                        double w = Math.log(gamma.get(layer).get(v.getId()) + Math.E);
                        double probUp = w / (w + 1);
                        if (rx > probUp && layer > initialLayer) {
                            --layer;
                        } else {
                            if (layer + 1 < layersAdj.size() && layersAdj.get(layer + 1).containsKey(v.getId())) {
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

    public static <K> void showEmbeddings(Map<K, List<Double>> embeddings, String labelPath, String outPath) throws IOException {
        Map<String, List<String>> labels = GraphHelper.readKVFile(labelPath);
        Map<String, List<Integer>> colorId = new HashMap<>();
        TSNE instance = new TSNE();
        instance.setTargetDimension(2);

        Matrix orig_dim = new DenseMatrix(embeddings.size(), embeddings.values().iterator().next().size());
        int i = 0, j = 0;
        for (Map.Entry<String, List<String>> item : labels.entrySet()) {
            List<Integer> vs = new ArrayList<>();
            colorId.put(item.getKey(), vs);
            for (String v : item.getValue()) {
                colorId.get(item.getKey()).add(i);
                j = 0;
                for (Double val : embeddings.get(v)) {
                    orig_dim.set(i, j++, val);
                }
                i++;
            }
        }
        SimpleDataSet proj = new SimpleDataSet(new CategoricalData[0], orig_dim.cols());
        for (i = 0; i < orig_dim.rows(); i++) {
            proj.add(new DataPoint(orig_dim.getRow(i)));
        }
        SimpleDataSet nodePosition = instance.transform(proj);

        XYSeriesCollection dataset = new XYSeriesCollection();
        colorId.forEach((label, ids) -> {
            XYSeries XY = new XYSeries(label);
            ids.forEach(id -> {
                XY.add(nodePosition.getDataPoint(id).getNumericalValues().get(0), nodePosition.getDataPoint(id).getNumericalValues().get(1));
            });
            dataset.addSeries(XY);
        });

        JFreeChart freeChart = ChartFactory.createScatterPlot(
            "embeddings",
            "X",
            "Y",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        OutputStream os_png = new FileOutputStream(outPath);
        ChartUtils.writeChartAsPNG(os_png, freeChart, 560, 400);

        ChartPanel chartPanel = new ChartPanel(freeChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 400));

        JFrame frame = new JFrame("embeddings");
        frame.setLocation(500, 400);
        frame.setSize(600, 500);
        frame.setContentPane(chartPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static <K> void evaluateEmbeddings(Map<K, List<Double>> embeddings, String labelPath) {
        Map<String, List<String>> labels = GraphHelper.readKVFile(labelPath);
        try {
            int xSize = embeddings.values().iterator().next().size();
            ArrayList<Attribute> attributes = new ArrayList<Attribute>();
            for (int i = 0; i < xSize; i++) {
                attributes.add(new Attribute("coordinate_" + i));
            }
            List<String> classes = new ArrayList<>(labels.keySet());
            attributes.add(new Attribute("class", classes));

            Instances instances = new Instances("graph", attributes, 0);
            instances.setClassIndex(instances.numAttributes() - 1);

            labels.forEach((label, vs) -> {
                double num[] = new double[instances.numAttributes()];
                Instance instance = new DenseInstance(1, num);
                vs.forEach(v -> {
                    for (int j = 0; j < xSize; j++) {
                        num[j] = embeddings.get(v).get(j);
                    }
                    num[instances.classIndex()] = instances.attribute(instances.classIndex()).indexOfValue(label);
                    instances.add(instance);
                });
            });

            //evaluate
            Evaluation eval = new Evaluation(instances);
            NaiveBayesUpdateable method = new NaiveBayesUpdateable();
            // J48 method = new J48();
            eval.crossValidateModel(method, instances, 10, new Random(1));
            System.out.println(eval.toMatrixString());
            System.out.println(eval.toSummaryString());
            System.out.println(eval.toClassDetailsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}