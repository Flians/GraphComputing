package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.arch.cstore.benchmark.GraphGenerator;
import com.antfin.graph.Graph;
import com.antfin.graph.newObj.*;
import org.apache.lucene.util.RamUsageEstimator;
import org.junit.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphTest_New {
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
        this.edges = ggen.getEdges(1000000, 20);
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
    public void test_EL() {
        System.out.println(">>> Graph_EL");
        this.graph = new Graph_EL(this.edges, false);

        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_EL) this.graph).getEdges()));
    }

    @Test
    public void test_CSR_EL_Map() throws IOException {
        System.out.println(">>> Graph_CSR_EL_Map");
        this.graph = new Graph_CSR_EL_Map(this.edges, false);

        FileWriter fw = new FileWriter(new File("graph.txt"));
        ((Graph_CSR_EL_Map) this.graph).getTargets().forEach((key, value) -> {
            ((List)value).forEach(t->{
                try {
                    fw.write(key + "," + t + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_EL_Map) this.graph).getDict_V()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_EL_Map) this.graph).getTargets()));
    }

    @Test
    public void test_CSR_GC_STD() {
        System.out.println(">>> Graph_CSR_GC_STD");
        this.graph = new Graph_CSR_GC_STD(this.edges, false);

        System.out.println("max gap: " + ((Graph_CSR_GC_STD) this.graph).reorder_BFS());

        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC_STD) this.graph).getDict_V()));
        System.out.println(RamUsageEstimator.humanSizeOf(((Graph_CSR_GC_STD) this.graph).getTargets()));
    }
}