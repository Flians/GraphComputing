package com.antfin.graph.newObj;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Flians
 * @Description: Compare the corresponding method in refObj
 * @Title: Graph
 * @ProjectName graphRE
 * @date 2020/5/25 10:30
 */

public class Graph_EL extends Graph {
    // record all edges; String -> edge.source.id
    private Map<String, List<String>> edges;

    public Graph_EL() {
        this.edges = new HashMap<>();
    }

    public Graph_EL(List vg, boolean flag) {
        this();
        if (flag) {
            ((List<Vertex>) vg).forEach(this::addVertex);
        } else {
            ((List<Edge>) vg).stream().forEach(this::addEdge);
        }
    }

    public Graph_EL(List<Vertex> vertices, List<Edge> edges) {
        this(vertices, true);
        edges.stream().forEach(this::addEdge);
    }

    @Override
    public void addVertex(Vertex vertex) {
        this.addVertex((String) vertex.getId());
    }

    public void addVertex(String id) {
        if (!this.edges.containsKey(id)) {
            this.edges.put(new String(id), new ArrayList<>());
        }
    }

    @Override
    public void addEdge(Edge edge) {
        this.addVertex((String) edge.getSrcId());
        this.addVertex((String) edge.getTargetId());
        this.edges.get(edge.getSrcId()).add(new String((String) edge.getTargetId()));
    }

    @Override
    public List<String> getVertex(Object key) {
        if (this.edges.containsKey(key))
            return this.edges.get(key);
        return null;
    }

    @Override
    public List<String> getEdge(Object sid) {
        return this.edges.get(sid);
    }

    @Override
    public void clear() {
        this.edges.clear();
        this.edges = null;
    }


    public Map<String, List<String>> getEdges() {
        return this.edges;
    }
}