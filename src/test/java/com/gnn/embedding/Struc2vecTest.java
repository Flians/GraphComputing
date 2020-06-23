package com.gnn.embedding;

import com.gnn.util.GNNHelper;
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
    public void test_struc2vec() throws IOException, InterruptedException {
        Struc2vec struc2vec = new Struc2vec("/Users/flynn/IdeaProjects/GraphComputing/src/test/data/brazil-airports.edgelist");
        struc2vec.train(128, 5, 4, 5);
        struc2vec.getEmbeddings();
        GNNHelper.showEmbeddings(struc2vec.getEmbeddings(), "/Users/flynn/IdeaProjects/GraphComputing/src/test/data/labels-brazil-airports.txt", struc2vec.getTempPath() + "embedding.png");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Struc2vec struc2vec = new Struc2vec("/Users/flynn/IdeaProjects/GraphComputing/src/test/data/brazil-airports.edgelist");
        struc2vec.train(128, 5, 4, 5);
        struc2vec.getEmbeddings();
        GNNHelper.showEmbeddings(struc2vec.getEmbeddings(), "/Users/flynn/IdeaProjects/GraphComputing/src/test/data/labels-brazil-airports.txt", struc2vec.getTempPath() + "embedding.png");
        GNNHelper.evaluateEmbeddings(struc2vec.getEmbeddings(), "/Users/flynn/IdeaProjects/GraphComputing/src/test/data/labels-brazil-airports.txt");
    }
}
