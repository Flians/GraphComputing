package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.arch.cstore.benchmark.GraphGenerator;
import com.antfin.graph.Graph;
import com.antfin.graph.refObj.*;
import com.antfin.util.GraphVerify;
import org.apache.lucene.util.RamUsageEstimator;
import org.junit.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphTest_Ref {
    Graph graph;
    List<Vertex<String, String>> vertices;
    List<Edge<String, String>> edges;
    Map<String, Map<String, Boolean>> sTt;
    int uniqueE;

    @BeforeClass
    public static void setUpBeforeClass() {
        System.out.println("this is @BeforeClass ...");
    }

    @AfterClass
    public static void tearDownAfterClass() {
        System.out.println("this is @AfterClass ...");
    }

    @Before
    public void setUp() {
        System.out.println("this is @Before ...");
        GraphGenerator ggen = new GraphGenerator();
        this.edges = ggen.getEdges(100000, 20);
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
    public void tearDown() {
        System.out.println("Memory size: " + RamUsageEstimator.humanSizeOf(this.graph));
        System.out.println("this is @After ...");
        this.graph.clear();
        this.graph = null;
        this.uniqueE = 0;
    }

    @Test
    public void test_Map_EL() {
        System.out.println(">>> Graph_Map_EL");
        this.graph = new Graph_Map_EL(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object key : ((Graph_Map_EL) this.graph).getVertices().keySet()) {
            numE += GraphVerify.verify((String) key, this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_EL) this.graph).getVertices()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_EL) this.graph).getEdges()));
    }

    @Test
    public void test_Map_CSR() {
        System.out.println(">>> Graph_Map_CSR");
        this.graph = new Graph_Map_CSR(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object v : ((Graph_Map_CSR) this.graph).getVertices()) {
            numE += GraphVerify.verify(((Vertex<String, String>) v).getId(), this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_CSR) this.graph).getVertices()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_CSR) this.graph).getEdges()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_CSR) this.graph).getDictV()));
    }

    @Test
    public void test_CSR_EL_Map() {
        System.out.println(">>> Graph_CSR_EL_Map");
        this.graph = new Graph_CSR_EL_Map(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object key : ((Graph_CSR_EL_Map) this.graph).getDict_V().keySet()) {
            numE += GraphVerify.verify((String) key, this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_EL_Map) this.graph).getDict_V()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_EL_Map) this.graph).getTargets()));
    }

    @Test
    public void test_CSR_EL_List() {
        System.out.println(">>> Graph_CSR_EL_List");
        this.graph = new Graph_CSR_EL_List(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object key : ((Graph_CSR_EL_List) this.graph).getDict_V().keySet()) {
            numE += GraphVerify.verify((String) key, this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_EL_List) this.graph).getDict_V()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_EL_List) this.graph).getTargets()));
    }

    @Test
    public void test_CSR_GC_Son() {
        System.out.println(">>> Graph_CSR_GC_Son");
        this.graph = new Graph_CSR_GC_Son(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object key : ((Graph_CSR_GC_Son) this.graph).getDict_V().keySet()) {
            numE += GraphVerify.verify((String) key, this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(">>> After reordering");
        System.out.println("max gap: " + ((Graph_CSR_GC_Son) this.graph).reorder_BFS());

        // verity the reordered graph
        numE = 0;
        for (Object key : ((Graph_CSR_GC_Son) this.graph).getDict_V().keySet()) {
            numE += GraphVerify.verify((String) key, this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println("Some edges are lost!");
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC_Son) this.graph).getDict_V()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC_Son) this.graph).getTargets()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC_Son) this.graph).getCsr()));
    }

    @Test
    public void test_CSR_GC_Brother() {
        System.out.println(">>> Graph_CSR_GC_Brother");
        this.graph = new Graph_CSR_GC_Brother(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object key : ((Graph_CSR_GC_Brother) this.graph).getDict_V().keySet()) {
            numE += GraphVerify.verify((String) key, this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(">>> After reordering");
        System.out.println("max gap: " + ((Graph_CSR_GC_Brother) this.graph).reorder_BFS());

        // verity the reordered graph
        numE = 0;
        for (Object key : ((Graph_CSR_GC_Brother) this.graph).getDict_V().keySet()) {
            numE += GraphVerify.verify((String) key, this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println("Some edges are lost!");
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC_Brother) this.graph).getDict_V()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC_Brother) this.graph).getTargets()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC_Brother) this.graph).getCsr()));
    }

    @Test
    public void test_CSR_GC() {
        System.out.println(">>> Graph_CSR_GC");
        this.graph = new Graph_CSR_GC(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object key : ((Graph_CSR_GC) this.graph).getDict_V().keySet()) {
            numE += GraphVerify.verify((String) key, this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(">>> After reordering");
        System.out.println("max gap: " + ((Graph_CSR_GC) this.graph).reorder_BFS());

        // verity the reordered graph
        numE = 0;
        for (Object key : ((Graph_CSR_GC) this.graph).getDict_V().keySet()) {
            numE += GraphVerify.verify((String) key, this.sTt, this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println("Some edges are lost!");
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC) this.graph).getDict_V()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC) this.graph).getTargets()));
    }


    /*
    @Test
    public void test_Map_CSR_N() {
        System.out.println(">>> Graph_Map_CSR_N");
        this.graph = new Graph_Map_CSR_N(this.edges, false);

        // verity the generated graph
        int numE = 0;
        for (Object v : ((Graph_Map_CSR_N) this.graph).getVertices()) {
            numE += GraphVerify.verify(((Vertex<String, String>) v).getId(), this.sTt,this.graph);
        }
        if (numE != this.uniqueE)
            System.out.println(" some edges are lost!");

        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_CSR_N) this.graph).getVertices()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_CSR_N) this.graph).getEdges()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_CSR_N) this.graph).getDict_V_alone()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_CSR_N) this.graph).getDict_V_edges()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_Map_CSR_N) this.graph).getCsr()));
    }
     */
}