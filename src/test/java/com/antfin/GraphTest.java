package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.arch.cstore.benchmark.GraphGenerator;

import org.apache.lucene.util.RamUsageEstimator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphTest {
    Graph graph;
    List<Vertex<String, String>> vertices;
    List<Edge<String, String>> edges;
    Map<String, Map<String, Boolean>> sTt;
    int uniqueE;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.out.println("this is @BeforeClass ...");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        System.out.println("this is @AfterClass ...");
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("this is @Before ...");
        GraphGenerator ggen = new GraphGenerator();
        this.edges = ggen.getEdges(10000, 20);
        this.sTt = new HashMap<>();
        this.edges.forEach(e -> {
            if (this.sTt.containsKey(e.getSrcId())) {
                this.sTt.get(e.getSrcId()).put(e.getTargetId(), true);
            } else {
                Map<String, Boolean> item = new HashMap<>();
                item.put(e.getTargetId(), true);
                this.sTt.put(e.getSrcId(), item);
            }
        });
        this.uniqueE = 0;
        for (String key : this.sTt.keySet()) {
            this.uniqueE += this.sTt.get(key).size();
        }
        System.out.println("The number of the unique edges is " + this.uniqueE);
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Memory size: " + RamUsageEstimator.humanSizeOf(this.graph));
        System.out.println("this is @After ...");
        this.graph.clear();
        this.graph = null;
        this.uniqueE = 0;
    }

    @Test
    public void test_TwoMap() throws IllegalAccessException {
        System.out.println(">>> Graph_TwoMap");
        this.graph = new Graph_TwoMap(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object key:((Graph_TwoMap) this.graph).getVertices().keySet()) {
            numE += verify((String) key);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_TwoMap) this.graph).getVertices()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_TwoMap) this.graph).getEdges()));
    }

    @Test
    public void test_CSR_N() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR_N");
        this.graph = new Graph_CSR_N(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object v : ((Graph_CSR_N) this.graph).getVertices()) {
            numE += verify(((Vertex<String, String>) v).getId());
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_N) this.graph).getVertices()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_N) this.graph).getEdges()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_N) this.graph).getDict_V_alone()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_N) this.graph).getDict_V_edges()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_N) this.graph).getCsr()));
    }

    @Test
    public void test_CSR() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR");
        this.graph = new Graph_CSR(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object v : ((Graph_CSR) this.graph).getVertices()) {
            numE += verify(((Vertex<String, String>) v).getId());
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR) this.graph).getVertices()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR) this.graph).getEdges()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR) this.graph).getDict_V()));
    }

    @Test
    public void test_CSR_GC() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR_GC");
        this.graph = new Graph_CSR_GC(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object key : ((Graph_CSR_GC) this.graph).getDict_V().keySet()) {
            numE += verify((String) key);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(">>> After reordering");
        System.out.println("max gap: " + ((Graph_CSR_GC) this.graph).reorder_BFS());

        // verity the reordered graph
        numE = 0;
        for (Object key : ((Graph_CSR_GC) this.graph).getDict_V().keySet()) {
            numE += verify((String) key);
        }
        if (numE != this.uniqueE)
            System.out.println("Some edges are lost!");
    }

    public int verify(String key) {
        int numE = 0;
        if (this.sTt.containsKey(key)) {
            numE = ((List) graph.getEdge(key)).size();
            if (this.graph instanceof Graph_CSR_GC || this.graph instanceof Graph_CSR_GC_BFS_Degree) {
                int sid = (int) this.graph.getVertex(key);
                ((List<Integer>) graph.getEdge(key)).forEach(gap -> {
                    if (!this.sTt.get(key).containsKey(this.graph.getVertex(gap + sid))) {
                        System.out.println("<" + key + "," + this.graph.getVertex(gap + sid) + "> is not existed!");
                    }
                });
            } else {
                ((List<Edge<String, String>>) graph.getEdge(key)).forEach(e -> {
                    if (!this.sTt.get(key).containsKey(e.getTargetId())) {
                        System.out.println("<" + key + "," + e.getTargetId() + "> is not existed!");
                    }
                });
            }
        } else {
            System.out.println(key + " has no output edges!");
        }
        return numE;
    }

    /*
    @Test
    public void test_CSR_GC_Degree() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR_GC_Degree");
        System.gc();
        long start = Runtime.getRuntime().freeMemory();
        this.graph = new Graph_CSR_GC_BFS_Degree(this.edges, false);
        System.gc();
        long end = Runtime.getRuntime().freeMemory();
        // 5166064
        System.out.println(end - start);

        // verity the generated graph
        int numE = 0;
        for (Object key : ((Graph_CSR_GC_BFS_Degree) this.graph).getDict_V().keySet()) {
            numE += verify((String) key);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(">>> After reordering");
        System.gc();
        start = Runtime.getRuntime().freeMemory();
        System.out.println("max gap: " + ((Graph_CSR_GC_BFS_Degree) this.graph).reorder_BFS_Degree());
        System.gc();
        end = Runtime.getRuntime().freeMemory();
        // 5166064
        System.out.println(end - start);

        // verity the generated graph
        numE = 0;
        for (Object key : ((Graph_CSR_GC_BFS_Degree) this.graph).getDict_V().keySet()) {
            numE += verify((String) key);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");


        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR_Reorder) graph).getTargets()) +
                SizeOfObject.fullSizeOf(((Graph_CSR_Reorder) graph).getDict_V()));

        // 3749472
        System.out.println(
                SizeOfObject.fullSizeOf(((Graph_CSR_Reorder) graph).getTargets().get(0)) * ((Graph_CSR_Reorder) graph).getTargets().size() +
                        SizeOfObject.fullSizeOf(((Graph_CSR_Reorder) graph).getDict_V().entrySet().iterator().next()) * ((Graph_CSR_Reorder) graph).getDict_V().size());
        // 13915050
        System.out.println(
                Arrays.toString(((Graph_CSR_Reorder) graph).getTargets().toArray()).getBytes().length +
                        Arrays.toString(((Graph_CSR_Reorder) graph).getDict_V().entrySet().toArray()).getBytes().length);

    }
    */
}