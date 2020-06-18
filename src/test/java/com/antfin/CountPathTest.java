package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.gnn.embedding.Struc2vec;
import com.antfin.graph.Graph;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CountPathTest<K, VV, EV> {
    Graph graph;
    List<Vertex> sources;
    List<Vertex> targets;

    public void countPath(int tranNum, List<Vertex> sources, List<Vertex> targets, Graph graph) {
        Queue<Vertex> cur = new LinkedList<>();
        cur.addAll(sources);
        Map<K, Map<K, Integer>> numIn = new HashMap<>();
        while (!cur.isEmpty() && (tranNum--) > 0) {
            int nums = cur.size();
            while ((nums--) > 0) {
                Vertex vertex = cur.poll();
                ((List<Edge<K, EV>>)graph.getEdge(vertex.getId())).forEach(e->{
                    if (numIn.containsKey(e.getTargetId())) {
                        if (numIn.get(e.getTargetId()).containsKey(vertex.getId())) {
                            numIn.get(e.getTargetId()).put((K) vertex.getId(), numIn.get(e.getTargetId()).get(vertex.getId()) + 1);
                        } else {
                            numIn.get(e.getTargetId()).put((K) vertex.getId(), 1);
                        }
                    } else {
                        Map<K, Integer> in = new HashMap<>();
                        in.put((K) vertex.getId(), 1);
                        numIn.put(e.getTargetId(), in);
                    }
                    cur.add((Vertex) graph.getVertex(e.getTargetId()));
                });
            }
        }
        cur.clear();
        cur.addAll(targets);
        Map<K, Map<K, Integer>> numOut = new HashMap<>();
        while (!cur.isEmpty()) {
            int nums = cur.size();
            while ((nums--) > 0) {
                Vertex vertex = cur.poll();
                if (numIn.containsKey(vertex.getId())) {
                    numIn.get(vertex.getId()).forEach((source, in) -> {

                    });
                    if (numOut.containsKey(vertex.getId())) {

                    } else {
                        Map<K, Integer> out = new HashMap<>();

                    }
                }
            }
        }
    }

    @Before
    public void setUp() {
        System.out.println("this is @Before ...");
    }

    @After
    public void tearDown() {
        System.out.println("this is @After ...");
    }

    @Test
    public void test_CountPath() throws IOException {

    }

}
