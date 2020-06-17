package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arch.cstore.benchmark.GraphGenerator;
import com.antfin.arch.cstore.benchmark.RandomWord;
import com.antfin.gnn.embedding.Struc2vec;
import com.antfin.graph.Graph;
import com.antfin.graph.refObj.Graph_Map_CSR;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.util.RamUsageEstimator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Struc2vecTest {

    @Before
    public void setUp() {
        System.out.println("this is @Before ...");
    }

    @After
    public void tearDown() {
        System.out.println("this is @After ...");
    }

    @Test
    public void test_struc2vec() throws IOException {
        Struc2vec struc2vec = new Struc2vec("/Users/flynn/IdeaProjects/GraphRepresentation/src/test/data/brazil-airports.edgelist");
        struc2vec.createContextGraph(10, 4);
    }
}
