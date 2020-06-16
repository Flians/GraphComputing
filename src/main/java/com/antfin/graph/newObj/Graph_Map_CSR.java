package com.antfin.graph.newObj;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.graph.Graph;
import com.antfin.util.GraphHelper;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @param <K>
 * @param <VV>
 * @param <EV>
 * @author Flians
 * @Description: The graph is described by Compressed Sparse Row (CSR).
 * When the vertex is assigned to a edge, it is saved into Map dict_V<vertex_id, value>.
 * The value is the index of this vertex in vertices, and is the index of its edges in edges.
 * Not good:
 * There are duplicate edges in edges
 * @Title: Graph
 * @ProjectName graphRE
 * @date 2020/5/18 14:29
 */
public class Graph_Map_CSR<K, VV, EV> extends Graph<K, VV, EV> {
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
            Vertex<K, VV> newV = new Vertex();
            try {
                BeanUtils.copyProperties(newV, vertex);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            this.vertices.add(newV);
            this.dict_V.put(newV.getId(), this.dict_V.size());
        }
    }

    @Override
    public void addEdge(Edge<K, EV> edge) {
        Edge<K, EV> newE = new Edge();
        try {
            BeanUtils.copyProperties(newE, edge);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // the source vertex of edge has no edges.
        if (!this.dict_V.containsKey(edge.getSrcId())) {
            Vertex<K, VV> newV = new Vertex();
            try {
                BeanUtils.copyProperty(newV, "id", edge.getSrcId());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            this.vertices.add(newV);
            this.dict_V.put(newV.getId(), this.edges.size());
            this.dict_V.put(this.vertices.get(this.edges.size()).getId(), this.dict_V.size() - 1);
            GraphHelper.swap(this.edges.size(), this.vertices.size() - 1, this.vertices);
            List<Edge<K, EV>> item = new ArrayList<>();
            item.add(newE);
            this.edges.add(item);
        } else {
            // the source vertex of edge has other edges.
            this.edges.get(this.dict_V.get(edge.getSrcId())).add(newE);
        }
    }

    @Override
    public Vertex<K, VV> getVertex(K id) {
        return this.vertices.get(this.dict_V.get(id));
    }

    @Override
    public List<Edge<K, EV>> getEdge(K sid) {
        return this.edges.get(this.dict_V.get(sid));
    }

    @Override
    public List<Object> getVertexList() {
        return new ArrayList<>(this.vertices);
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