package com.antfin.graph.refObj;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.graph.Graph;

import java.util.*;

/**
 * @param <K>
 * @param <VV>
 * @param <EV>
 * @author Flians
 * @Description: The graph is described by Compressed Sparse Row (CSR).
 * To reduce the space of List csr, Map dict_V_alone is used to save the isolated vertices.
 * When this vertex is assigned to a edge, it is saved into Map dict_V_edges, and this edge is mapped into List csr.
 * Not good:
 *      O(N) for querying the vertex.
 *      There are duplicate edges in edges
 * @Title: Graph
 * @ProjectName graphRE
 * @date 2020/5/14 16:37
 */
public class Graph_Map_CSR_N<K, VV, EV> extends Graph<K, VV, EV> {
    private List<Vertex<K, VV>> vertices;
    private List<List<Edge<K, EV>>> edges;
    // dict_V_edges[vertex.id] -> index, record the vertices with edges
    private Map<K, Integer> dict_V_edges;
    // dict_V_alone[vertex.id] -> index, record the the vertices without edges
    private Map<K, Integer> dict_V_alone;
    // csr[index] is the start position of the output edges of vertex in edges
    private List<Integer> csr;

    public Graph_Map_CSR_N() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.dict_V_edges = new HashMap<>();
        this.dict_V_alone = new HashMap<>();
        this.csr = new ArrayList<>();
    }

    public Graph_Map_CSR_N(List vg, boolean flag) {
        this();
        if (flag) {
            ((List<Vertex<K, VV>>) vg).forEach(this::addVertex);
        } else {
            ((List<Edge<K, EV>>) vg).forEach(this::addEdge);
        }
    }

    public Graph_Map_CSR_N(List<Vertex<K, VV>> vertices, List<Edge<K, EV>> edges) {
        this(vertices, true);
        edges.stream().forEach(this::addEdge);
    }

    @Override
    public void addVertex(Vertex<K, VV> vertex) {
        if (!this.dict_V_edges.containsKey(vertex.getId()) && !this.dict_V_alone.containsKey(vertex.getId())) {
            this.vertices.add(vertex);
            this.dict_V_alone.put(vertex.getId(), this.dict_V_alone.size());
        }
    }

    @Override
    public void addEdge(Edge<K, EV> edge) {
        // the source vertex of edge has no edges.
        if (!this.dict_V_edges.containsKey(edge.getSrcId())) {
            if (this.dict_V_alone.containsKey(edge.getSrcId())) {
                this.dict_V_alone.remove(edge.getSrcId());
            } else {
                this.vertices.add(new Vertex<>(edge.getSrcId()));
            }
            this.dict_V_edges.put(edge.getSrcId(), this.csr.size());
            this.csr.add(this.edges.size());
            List<Edge<K, EV>> item = new ArrayList<>();
            item.add(edge);
            this.edges.add(item);
        } else {
            // the source vertex of edge has other edges.
            this.edges.get(this.csr.get(this.dict_V_edges.get(edge.getSrcId()))).add(edge);
        }
    }

    @Override
    public Vertex<K, VV> getVertex(K id) {
        Optional<Vertex<K, VV>> res = this.vertices.stream().filter(vertex -> vertex.getId().equals(id)).findFirst();
        return res.get();
    }

    @Override
    public List<Edge<K, EV>> getEdge(K sid) {
        return this.edges.get(this.csr.get(this.dict_V_edges.get(sid)));
    }

    @Override
    public void clear() {
        this.csr.clear();
        this.csr = null;
        this.dict_V_alone.clear();
        this.dict_V_alone = null;
        this.dict_V_edges.clear();
        this.dict_V_edges = null;
        this.edges.clear();
        this.edges = null;
        this.vertices.clear();
        this.vertices = null;
    }

    public List<Vertex<K, VV>> getVertices() {
        return this.vertices;
    }

    public List<List<Edge<K, EV>>> getEdges() {
        return this.edges;
    }

    public Map<K, Integer> getDict_V_edges() {
        return this.dict_V_edges;
    }

    public Map<K, Integer> getDict_V_alone() {
        return this.dict_V_alone;
    }

    public List<Integer> getCsr() {
        return this.csr;
    }
}