package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Flians
 * @Description: The graph is described by two maps.
 * @Title: Graph
 * @ProjectName graphRE
 * @date 2020/5/14 09:53
 * @param <K>
 *     The type of the id of the Vertex
 * @param <VV>
 *     The type of the Value of the Vertex
 * @param <EV>
 *     The type of the Value of the Edge
 */

public class Graph_TwoMap<K, VV, EV> extends Graph<K, VV, EV> {
    // record all vertices; K -> vertex.id
    private Map<K, Vertex<K, VV> > vertices;
    // record all edges; K -> edge.source.id
    private Map<K, List<Edge<K, EV> > > edges;

    public Graph_TwoMap() {
        this.vertices = new HashMap<>();
        this.edges = new HashMap<>();
    }

    public Graph_TwoMap(List vg, boolean flag) {
        this();
        if (flag) {
            this.vertices = ((List<Vertex<K, VV>>)vg).stream().collect(Collectors.toMap(Vertex<K, VV>::getId, Function.identity(), (k1, k2) -> k1));
        } else {
            ((List<Edge<K, EV>>)vg).stream().forEach(this::addEdge);
        }
    }

    public Graph_TwoMap(List<Vertex<K, VV>> vertices, List<Edge<K, EV>> edges) {
        this(vertices, true);
        edges.stream().forEach(this::addEdge);
    }

    @Override
    public void addVertex(Vertex<K, VV> vertex) {
        if (!this.vertices.containsKey(vertex.getId())) {
            this.vertices.put(vertex.getId(), vertex);
        }
    }

    @Override
    public void addEdge(Edge<K, EV> edge) {
        // new Vertex if srcId is not existed
        if (!this.vertices.containsKey(edge.getSrcId())) {
            this.vertices.put(edge.getSrcId(), new Vertex(edge.getSrcId()));
        }
        if (this.edges.containsKey(edge.getSrcId())) {
            // prevent repetition
            if (!this.edges.get(edge.getSrcId()).contains(edge))
                this.edges.get(edge.getSrcId()).add(edge);
        } else {
            List<Edge<K, EV> > temp = new ArrayList<>();
            temp.add(edge);
            this.edges.put(edge.getSrcId(), temp);
        }
    }

    @Override
    public Vertex<K, VV> getVertex(K id) {
        return this.vertices.get(id);
    }

    @Override
    public List<Edge<K, EV> > getEdge(K sid) {
        return this.edges.get(sid);
    }

    public Map<K, Vertex<K, VV>> getVertices() {
        return this.vertices;
    }

    public Map<K, List<Edge<K, EV> > > getEdges() {
        return this.edges;
    }
}