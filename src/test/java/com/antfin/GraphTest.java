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
        System.out.println("this is @After ...");
        this.graph.clear();
        this.graph = null;
        this.uniqueE = 0;
    }

    @Test
    public void test_TwoMap() throws IllegalAccessException {
        System.out.println(">>> Graph_TwoMap");
        System.gc();
        long start = Runtime.getRuntime().freeMemory();
        this.graph = new Graph_TwoMap(this.edges, false);
        System.gc();
        long end = Runtime.getRuntime().freeMemory();
        // 5250376
        // 45078192
        System.out.println(end - start);

        // verity the generated graph
        int numE = 0;
        for (Object key:((Graph_TwoMap) this.graph).getVertices().keySet()) {
            numE += verify((String) key);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        // 2738480
        System.out.print("lucene: ");
        System.out.println(RamUsageEstimator.sizeOfMap(((Graph_TwoMap) graph).getEdges()));
        System.out.println(RamUsageEstimator.sizeOfMap(((Graph_TwoMap) graph).getVertices()));
        System.out.println(RamUsageEstimator.sizeOfMap(((Graph_TwoMap) graph).getVertices()) + RamUsageEstimator.sizeOfMap(((Graph_TwoMap) graph).getEdges()));
        /*
        // 5742408
        System.out.println(SizeOfObject.fullSizeOf(((Graph_TwoMap) graph).getVertices().entrySet().iterator().next())*((Graph_TwoMap) graph).getVertices().size() +
                SizeOfObject.fullSizeOf(((Graph_TwoMap) graph).getEdges().entrySet().iterator().next())*((Graph_TwoMap) graph).getEdges().size());
        // 13917656
        System.out.println(Arrays.toString(((Graph_TwoMap) graph).getVertices().entrySet().toArray()).getBytes().length +
                Arrays.toString(((Graph_TwoMap) graph).getEdges().entrySet().toArray()).getBytes().length);
         */
    }

    @Test
    public void test_CSR_N() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR_N");
        System.gc();
        long start = Runtime.getRuntime().freeMemory();
        this.graph = new Graph_CSR_N(this.edges, false);
        System.gc();
        long end = Runtime.getRuntime().freeMemory();
        // 5254920
        // 29439056
        System.out.println(end - start);

        // verity the generated graph
        int numE = 0;
        for (Object v : ((Graph_CSR_N) this.graph).getVertices()) {
            numE += verify(((Vertex<String, String>) v).getId());
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        // 3054872
        System.out.print("lucene: ");
        String t1 = "2088977570432201";
        Integer t2 = 1;
        System.out.println(RamUsageEstimator.sizeOfObject(t1) + " " + RamUsageEstimator.sizeOfObject(t2));
        System.out.println(RamUsageEstimator.sizeOfCollection(((Graph_CSR_N) graph).getCsr()) + " " + ((Graph_CSR_N) this.graph).getCsr().size());
        System.out.println(RamUsageEstimator.sizeOfMap(((Graph_CSR_N) graph).getDict_V_edges()) +
                RamUsageEstimator.sizeOfMap(((Graph_CSR_N) graph).getDict_V_alone()));
        System.out.println(RamUsageEstimator.sizeOfCollection(((Graph_CSR_N) graph).getVertices()) +
                RamUsageEstimator.sizeOfCollection(((Graph_CSR_N) graph).getEdges()) +
                RamUsageEstimator.sizeOfCollection(((Graph_CSR_N) graph).getCsr()) +
                RamUsageEstimator.sizeOfMap(((Graph_CSR_N) graph).getDict_V_edges()) +
                RamUsageEstimator.sizeOfMap(((Graph_CSR_N) graph).getDict_V_alone()));
        /*
        // 54460800
        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getVertices().get(0)) * ((Graph_CSR_N) graph).getVertices().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getEdges().get(0)) * ((Graph_CSR_N) graph).getEdges().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getCsr().get(0)) * ((Graph_CSR_N) graph).getCsr().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getDict_V_edges().entrySet().iterator().next()) * ((Graph_CSR_N) graph).getDict_V_edges().size() +
                (((Graph_CSR_N) graph).getDict_V_alone().isEmpty()?0:SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getDict_V_alone().entrySet().iterator().next()) * ((Graph_CSR_N) graph).getDict_V_alone().size()));

        // 13945114
        System.out.println(Arrays.toString(((Graph_CSR_N) graph).getVertices().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_N) graph).getEdges().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_N) graph).getCsr().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_N) graph).getDict_V_alone().entrySet().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_N) graph).getDict_V_edges().entrySet().toArray()).getBytes().length);
         */
    }

    @Test
    public void test_CSR() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR");
        System.gc();
        long start = Runtime.getRuntime().freeMemory();
        this.graph = new Graph_CSR(this.edges, false);
        System.gc();
        long end = Runtime.getRuntime().freeMemory();
        // 5251064
        // 19113392
        System.out.println(end - start);

        // verity the generated graph
        int numE = 0;
        for (Object v : ((Graph_CSR) this.graph).getVertices()) {
            numE += verify(((Vertex<String, String>) v).getId());
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        // 2925808
        System.out.print("lucene: ");
        System.out.println(RamUsageEstimator.sizeOfCollection(((Graph_CSR) graph).getVertices()) +
                RamUsageEstimator.sizeOfCollection(((Graph_CSR) graph).getEdges()) +
                RamUsageEstimator.sizeOfMap(((Graph_CSR) graph).getDict_V()));
        /*
        // 81747832
        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR) graph).getVertices().get(0)) * ((Graph_CSR) graph).getVertices().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR) graph).getEdges().get(0)) * ((Graph_CSR) graph).getEdges().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR) graph).getDict_V().entrySet().iterator().next()) * ((Graph_CSR) graph).getDict_V().size());
        // 13916025
        System.out.println(Arrays.toString(((Graph_CSR) graph).getVertices().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR) graph).getEdges().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR) graph).getDict_V().entrySet().toArray()).getBytes().length);
         */
    }

    @Test
    public void test_CSR_GC() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR_GC");
        System.gc();
        long start = Runtime.getRuntime().freeMemory();
        this.graph = new Graph_CSR_GC(this.edges, false);
        System.gc();
        long end = Runtime.getRuntime().freeMemory();
        // 2259176
        System.out.println(end - start);

        // verity the generated graph
        int numE = 0;
        for (Object key : ((Graph_CSR_GC) this.graph).getDict_V().keySet()) {
            numE += verify((String) key);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");
        // 2932696
        System.out.print("lucene: ");
        System.out.println(RamUsageEstimator.sizeOfCollection(((Graph_CSR_GC) graph).getCsr()) +
                RamUsageEstimator.sizeOfCollection(((Graph_CSR_GC) graph).getTargets()) +
                RamUsageEstimator.sizeOfMap(((Graph_CSR_GC) graph).getDict_V()));

        System.out.println(">>> After reordering");
        System.gc();
        start = Runtime.getRuntime().freeMemory();
        System.out.println("max gap: " + ((Graph_CSR_GC) this.graph).reorder_BFS());
        System.gc();
        end = Runtime.getRuntime().freeMemory();
        // 5166064
        System.out.println(end - start);

        // verity the reordered graph
        numE = 0;
        for (Object key : ((Graph_CSR_GC) this.graph).getDict_V().keySet()) {
            numE += verify((String) key);
        }
        if (numE != this.uniqueE)
            System.out.println("Some edges are lost!");

        /*
        // 3749472
        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR_GC) graph).getCsr().get(0)) * ((Graph_CSR_GC) graph).getCsr().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_GC) graph).getTargets().get(0)) * ((Graph_CSR_GC) graph).getTargets().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_GC) graph).getDict_V().entrySet().iterator().next()) * ((Graph_CSR_GC) graph).getDict_V().size());
        // 789041
        System.out.println(Arrays.toString(((Graph_CSR_GC) graph).getCsr().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_GC) graph).getTargets().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_GC) graph).getDict_V().entrySet().toArray()).getBytes().length);
         */
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