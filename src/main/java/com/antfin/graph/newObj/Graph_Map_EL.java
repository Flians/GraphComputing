package com.antfin.graph.newObj;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.graph.Graph;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @param <K>  The type of the id of the Vertex
 * @param <VV> The type of the Value of the Vertex
 * @param <EV> The type of the Value of the Edge
 * @author Flians
 * @Description: The graph is described by edge list.
 * For strings, all instances with the same value point to the same String except for instances created by new String(arg).
 * @Title: Graph
 * @ProjectName graphRE
 * @date 2020/5/14 09:53
 */

public class Graph_Map_EL<K, VV, EV> extends Graph<K, VV, EV> {
    // record all vertices; K -> vertex.id
    private Map<K, Vertex<K, VV>> vertices;
    // record all edges; K -> edge.source.id
    private Map<K, List<Edge<K, EV>>> edges;

    public Graph_Map_EL() {
        this.vertices = new HashMap<>();
        this.edges = new HashMap<>();
    }

    public Graph_Map_EL(List vg, boolean flag) {
        this();
        if (flag) {
            this.vertices = ((List<Vertex<K, VV>>) vg).stream().collect(Collectors.toMap(Vertex<K, VV>::getId, Function.identity(), (k1, k2) -> k1));
        } else {
            ((List<Edge<K, EV>>) vg).stream().forEach(this::addEdge);
        }
    }

    public Graph_Map_EL(List<Vertex<K, VV>> vertices, List<Edge<K, EV>> edges) {
        this(vertices, true);
        edges.stream().forEach(this::addEdge);
    }

    @Override
    public void addVertex(Vertex<K, VV> vertex) {
        if (!this.vertices.containsKey(vertex.getId())) {
            Vertex<K, VV> newV = new Vertex();
            try {
                BeanUtils.copyProperties(newV, vertex);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            String newId = new String((byte[]) newV.getId());
            this.vertices.put((K) newId, newV);
        }
    }

    public void addVertex(K id) {
        if (!this.vertices.containsKey(id)) {
            Vertex<K, VV> newV = new Vertex();
            try {
                BeanUtils.copyProperty(newV, "id", id);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            String newId = new String((String) newV.getId());
            this.vertices.put((K) newId, newV);
        }
    }

    @Override
    public void addEdge(Edge<K, EV> edge) {
        this.addVertex(edge.getSrcId());
        Edge<K, EV> newE = new Edge();
        try {
            BeanUtils.copyProperties(newE, edge);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (this.edges.containsKey(edge.getSrcId())) {
            this.edges.get(edge.getSrcId()).add(newE);
        } else {
            List<Edge<K, EV>> temp = new ArrayList<>();
            temp.add(newE);
            String newId = new String((String) newE.getSrcId());
            this.edges.put((K) newId, temp);
        }
    }

    @Override
    public Vertex<K, VV> getVertex(K id) {
        return this.vertices.get(id);
    }

    @Override
    public List<Edge<K, EV>> getEdge(K sid) {
        return this.edges.get(sid);
    }

    @Override
    public List<Object> getVertexList() {
        return new ArrayList<>(this.vertices.values());
    }

    @Override
    public void clear() {
        this.edges.clear();
        this.edges = null;
        this.vertices.clear();
        this.vertices = null;
    }

    public Map<K, Vertex<K, VV>> getVertices() {
        return this.vertices;
    }

    public Map<K, List<Edge<K, EV>>> getEdges() {
        return this.edges;
    }
}