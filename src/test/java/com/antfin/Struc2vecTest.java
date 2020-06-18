package com.antfin;

import com.gnn.embedding.Struc2vec;
import java.io.IOException;
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
