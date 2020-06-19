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
    private Map<K, Integer> dictV;

    public Graph_Map_CSR() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.dictV = new HashMap<>();
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
        if (!this.dictV.containsKey(vertex.getId())) {
            this.vertices.add(vertex);
            this.dictV.put(vertex.getId(), this.dictV.size());
        } else {
            if (vertex.isEmpty() && this.dictV.get(vertex.getId()) < this.edges.size()) {
                this.vertices.set(this.dictV.get(vertex.getId()), nullVertex);
            } else {
                this.vertices.set(this.dictV.get(vertex.getId()), vertex);
            }
        }
    }

    @Override
    public void addEdge(Edge<K, EV> edge) {
        K srcId = edge.getSrcId();
        boolean exist = dictV.containsKey(srcId);
        if (exist && this.dictV.get(srcId) < this.edges.size()) {
            // the source vertex of edge has other edges.
            this.edges.get(this.dictV.get(srcId)).add(edge);
        } else {
            int dictVal = 0;
            if (!exist) {
                this.vertices.add(nullVertex);
                dictVal = this.dictV.size();
            } else {
                dictVal = this.dictV.get(srcId);
                if (this.vertices.get(dictVal) != nullVertex && this.vertices.get(dictVal).isEmpty()) {
                    this.vertices.set(dictVal, nullVertex);
                }
            }
            if (this.edges.size() != this.dictV.size() && dictVal != this.edges.size()) {
                this.dictV.put(this.vertices.get(this.edges.size()).getId(), dictVal);
                GraphHelper.swap(this.edges.size(), dictVal, this.vertices);
            }
            this.dictV.put(srcId, this.edges.size());

            List<Edge<K, EV>> edges = new ArrayList<>();
            edges.add(edge);
            this.edges.add(edges);
        }
        // add target
        if (!dictV.containsKey(edge.getTargetId())) {
            this.vertices.add(new Vertex<>(edge.getTargetId()));
            this.dictV.put(edge.getTargetId(), this.dictV.size());
        }
    }

    @Override
    public Vertex<K, VV> getVertex(K sid) {
        Vertex<K, VV> result = this.dictV.containsKey(sid) ? this.vertices.get(this.dictV.get(sid)) : null;
        if (result == nullVertex) {
            return new Vertex<>(sid);
        } else {
            return result;
        }
    }

    @Override
    public List<Edge<K, EV>> getEdge(K sid) {
        if (this.dictV.containsKey(sid) && this.dictV.get(sid) < this.edges.size()) {
            return this.edges.get(this.dictV.get(sid)).stream()
                .map(x -> {
                    x.setSrcId(sid);
                    return x;
                })
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Object> getVertexList() {
        return this.dictV.entrySet().stream().map(entry -> {
            Vertex<K, VV> vertex = vertices.get(dictV.get(entry.getKey()));
            if (vertex == nullVertex){
                return new Vertex<K, VV>(entry.getKey());
            } else {
                return vertex;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        this.dictV.clear();
        this.dictV = null;
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

    public Map<K, Integer> getDictV() {
        return this.dictV;
    }
}