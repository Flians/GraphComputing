package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;

import java.util.*;

/**
 * @author Flians
 * @Description: the base class for the Graph
 * @Title: Graph
 * @ProjectName graphRE
 * @date 2020/5/14 17:57
 */
public abstract class Graph<K, VV, EV> {
    public abstract void addVertex(Vertex<K, VV> vertex);
    public abstract void addEdge(Edge<K, EV> edge);
    public abstract Object getVertex(K id);
    public abstract Object getEdge(K sid);
    public abstract void clear();
}
