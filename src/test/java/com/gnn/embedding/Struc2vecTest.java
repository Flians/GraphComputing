package com.gnn.embedding;

import com.gnn.embedding.Struc2vec;
import java.io.IOException;
import java.util.List;
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
    public void test_struc2vec() throws IOException, InterruptedException {
        Struc2vec struc2vec = new Struc2vec("/Users/flynn/IdeaProjects/GraphRepresentation/src/test/data/brazil-airports.edgelist");
        struc2vec.train(128, 5, 4, 5);
        struc2vec.getEmbeddings();
    }
}
