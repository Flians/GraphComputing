package com.antfin.graph.refObj;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.graph.Graph;
import com.antfin.util.GraphHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @param <K>
 * @param <VV>
 * @param <EV>
 * @author Flians
 * @Description: The graph is described by Compressed Sparse Row (CSR).
 * When the vertex is assigned to a edge, it is saved into Map dict_V<vertex_id, value>.
 * The value is the index of this vertex in vertices, and is the index of its edges in edges.
 * Not good:
 *      There are duplicate edges in edges
 * @Title: Graph
 * @ProjectName graphRE
 * @date 2020/5/18 14:29
 */
public class Graph_Map_CSR<K, VV, EV> extends Graph<K, VV, EV> {
    private static Vertex nullVertex = new Vertex();
    private List<Vertex<K, VV>> vertices;
    private List<List<Edge<K, EV>>> edges;
    // dict_V[vertex.id] -> index, record all vertices
    private Map<K, Integer> dict_V;

    public Graph_Map_CSR() {
        this.vertices = new LinkedList<>();
        this.edges = new ArrayList<>();
        this.dict_V = new HashMap<>();
    }

    public Graph_Map_CSR(List vg, boolean flag) {
        this();
        if (flag) {
            ((List<Vertex<K, VV>>) vg).forEach(this::addVertex);
        } else {
            ((List<Edge<K, EV>>) vg).forEach(this::addEdge);
        }
    }

    public Graph_Map_CSR(List<Vertex<K, VV>> vertices, List<Edge<K, EV>> edges) {
        this(vertices, true);
        edges.stream().forEach(this::addEdge);
    }

    @Override
    public void addVertex(Vertex<K, VV> vertex) {
        if (!this.dict_V.containsKey(vertex.getId())) {
            if (vertex.isEmpty()) {
                this.vertices.add(nullVertex);
            } else {
                this.vertices.add(vertex);
            }
            this.dict_V.put(vertex.getId(), this.dict_V.size());
        } else {
            if (vertex.isEmpty()) {
                this.vertices.set(this.dict_V.get(vertex.getId()), nullVertex);
            } else {
                this.vertices.set(this.dict_V.get(vertex.getId()), vertex);
            }
        }
    }

    @Override
    public void addEdge(Edge<K, EV> edge) {
        K srcId = edge.getSrcId();
        boolean exist = dict_V.containsKey(srcId);
        if (exist && this.dict_V.get(srcId) < this.edges.size()) {
            // the source vertex of edge has other edges.
            this.edges.get(this.dict_V.get(srcId)).add(edge);
        } else {
            int _id = 0;
            if (!exist) {
                this.vertices.add(nullVertex);
                if (this.dict_V.size() > 0) {
                    _id = this.dict_V.size() - 1;
                }
            } else {
                _id = this.dict_V.get(srcId);
            }
            this.dict_V.put(srcId, this.edges.size());
            this.dict_V.put(this.vertices.get(this.edges.size()).getId(), _id);
            GraphHelper.swap(this.edges.size(), _id, this.vertices);

            List<Edge<K, EV>> edges = new ArrayList<>();
            edges.add(edge);
            this.edges.add(edges);
        }
    }

    @Override
    public Vertex<K, VV> getVertex(K sid) {
        Vertex<K, VV> result = this.dict_V.containsKey(sid) ? this.vertices.get(this.dict_V.get(sid)) : null;
        if (result == nullVertex) {
            return new Vertex<>(sid);
        } else {
            return result;
        }
    }

    @Override
    public List<Edge<K, EV>> getEdge(K sid) {
        if (this.dict_V.containsKey(sid) && this.dict_V.get(sid) < this.edges.size()) {
            return this.edges.get(this.dict_V.get(sid)).stream()
                .map(x -> {
                    x.setSrcId(sid);
                    return x;
                })
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void clear() {
        this.dict_V.clear();
        this.dict_V = null;
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

    public Map<K, Integer> getDict_V() {
        return this.dict_V;
    }
}