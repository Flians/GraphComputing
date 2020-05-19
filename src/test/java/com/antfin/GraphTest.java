package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.arch.cstore.benchmark.GraphGenerator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GraphTest {
    Graph graph;
    List<Vertex<String, String>> vertices;
    List<Edge<String, String>> edges;

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
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("this is @After ...");
    }

    @Test
    public void test_TwoMap() throws IllegalAccessException {
        System.out.println(">>> Graph_TwoMap");
        System.gc();
        long start = Runtime.getRuntime().freeMemory();
        this.graph = new Graph_TwoMap(this.edges, false);
        System.gc();
        long end = Runtime.getRuntime().freeMemory();
        // 14844024
        System.out.println(end - start);

        System.out.println(SizeOfObject.fullSizeOf(((Graph_TwoMap) graph).getVertices()) + SizeOfObject.fullSizeOf(((Graph_TwoMap) graph).getEdges()));
        /*
        // 5742408
        System.out.println(SizeOfObject.fullSizeOf(((Graph_TwoMap) graph).getVertices().entrySet().iterator().next())*((Graph_TwoMap) graph).getVertices().size() +
                SizeOfObject.fullSizeOf(((Graph_TwoMap) graph).getEdges().entrySet().iterator().next())*((Graph_TwoMap) graph).getEdges().size());
        // 13917328
        System.out.println(Arrays.toString(((Graph_TwoMap) graph).getVertices().entrySet().toArray()).getBytes().length +
                Arrays.toString(((Graph_TwoMap) graph).getEdges().entrySet().toArray()).getBytes().length);
                */
        this.graph = null;
    }

    @Test
    public void test_CSR_N() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR_N");
        System.gc();
        long start = Runtime.getRuntime().freeMemory();
        this.graph = new Graph_CSR_N(this.edges, false);
        System.gc();
        long end = Runtime.getRuntime().freeMemory();
        // 14816816
        System.out.println(end - start);

        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getVertices()) +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getEdges()) +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getCsr()) +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getDict_V_edges()) +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getDict_V_alone()));
        /*
        // 54460800
        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getVertices().get(0)) * ((Graph_CSR_N) graph).getVertices().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getEdges().get(0)) * ((Graph_CSR_N) graph).getEdges().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getCsr().get(0)) * ((Graph_CSR_N) graph).getCsr().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getDict_V_edges().entrySet().iterator().next()) * ((Graph_CSR_N) graph).getDict_V_edges().size() +
                (((Graph_CSR_N) graph).getDict_V_alone().isEmpty()?0:SizeOfObject.fullSizeOf(((Graph_CSR_N) graph).getDict_V_alone().entrySet().iterator().next()) * ((Graph_CSR_N) graph).getDict_V_alone().size()));
        // 13943907
        System.out.println(Arrays.toString(((Graph_CSR_N) graph).getVertices().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_N) graph).getEdges().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_N) graph).getCsr().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_N) graph).getDict_V_alone().entrySet().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_N) graph).getDict_V_edges().entrySet().toArray()).getBytes().length);
                */
        this.graph = null;
    }

    @Test
    public void test_CSR() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR");
        System.gc();
        long start = Runtime.getRuntime().freeMemory();
        this.graph = new Graph_CSR(this.edges, false);
        System.gc();
        long end = Runtime.getRuntime().freeMemory();
        // 14825208
        System.out.println(end - start);

        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR) graph).getVertices()) +
                SizeOfObject.fullSizeOf(((Graph_CSR) graph).getEdges()) +
                SizeOfObject.fullSizeOf(((Graph_CSR) graph).getDict_V()) + SizeOfObject.fullSizeOf(((Graph_CSR) graph).getLastV()));
        /*
        // 81747832
        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR) graph).getVertices().get(0)) * ((Graph_CSR) graph).getVertices().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR) graph).getEdges().get(0)) * ((Graph_CSR) graph).getEdges().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR) graph).getDict_V().entrySet().iterator().next()) * ((Graph_CSR) graph).getDict_V().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR) graph).getLastV()));
        // 13915050
        System.out.println(Arrays.toString(((Graph_CSR) graph).getVertices().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR) graph).getEdges().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR) graph).getDict_V().entrySet().toArray()).getBytes().length);
                */
        this.graph = null;
    }

    @Test
    public void test_CSR_GC() throws IllegalAccessException {
        System.out.println(">>> Graph_CSR_GC");
        System.gc();
        long start = Runtime.getRuntime().freeMemory();
        this.graph = new Graph_CSR_GC(this.edges, false);
        System.gc();
        long end = Runtime.getRuntime().freeMemory();
        // 12927880
        System.out.println(end - start);

        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR_GC) graph).getCsr()) +
                SizeOfObject.fullSizeOf(((Graph_CSR_GC) graph).getTargets()) +
                SizeOfObject.fullSizeOf(((Graph_CSR_GC) graph).getDict_V()));
        /*
        // 3749472
        System.out.println(SizeOfObject.fullSizeOf(((Graph_CSR_GC) graph).getCsr().get(0)) * ((Graph_CSR_GC) graph).getCsr().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_GC) graph).getTargets().get(0)) * ((Graph_CSR_GC) graph).getTargets().size() +
                SizeOfObject.fullSizeOf(((Graph_CSR_GC) graph).getDict_V().entrySet().iterator().next()) * ((Graph_CSR_GC) graph).getDict_V().size());
        // 13915050
        System.out.println(Arrays.toString(((Graph_CSR_GC) graph).getCsr().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_GC) graph).getTargets().toArray()).getBytes().length +
                Arrays.toString(((Graph_CSR_GC) graph).getDict_V().entrySet().toArray()).getBytes().length);
        */
        this.graph = null;
    }
}