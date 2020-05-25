package com.antfin.graph.ref;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.graph.Graph;
import com.antfin.util.BiHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Flians
 * @Description: The graph is described by edge list.
 *               For strings, all instances with the same value point to the same String except for instances created by new String(ar).
 * @Title: Graph
 * @ProjectName graphRE
 * @date 2020/5/25 10:30
 * @param <K>
 *     The type of the id of the Vertex
 * @param <VV>
 *     The type of the Value of the Vertex
 * @param <EV>
 *     The type of the Value of the Edge
 */

public class Graph_CSR_EL_List<K, VV, EV> extends Graph<K, VV, EV> {
    // record all vertices; K -> vertex.id
    private Map<K, Integer > dict_V;
    // record all edges; K -> edge.source.id
    private List<List<Integer> > targets;

    public Graph_CSR_EL_List() {
        this.dict_V = new BiHashMap<>();
        this.targets = new ArrayList<>();
    }

    public Graph_CSR_EL_List(List vg, boolean flag) {
        this();
        if (flag) {
            ((List<Vertex<K, VV>>) vg).forEach(this::addVertex);
        } else {
            ((List<Edge<K, EV>>)vg).stream().forEach(this::addEdge);
        }
    }

    public Graph_CSR_EL_List(List<Vertex<K, VV>> vertices, List<Edge<K, EV>> edges) {
        this(vertices, true);
        edges.stream().forEach(this::addEdge);
    }

    @Override
    public void addVertex(Vertex<K, VV> vertex) {
        this.addVertex(vertex.getId());
    }

    public void addVertex(K id) {
        if (!this.dict_V.containsKey(id)) {
            this.dict_V.put(id, this.dict_V.size());
            this.targets.add(new ArrayList<>());
        }
    }

    @Override
    public void addEdge(Edge<K, EV> edge) {
        this.addVertex(edge.getSrcId());
        this.addVertex(edge.getTargetId());
        this.targets.get(this.dict_V.get(edge.getSrcId())).add(this.dict_V.get(edge.getTargetId()));
    }

    @Override
    public Object getVertex(Object key) {
        if (key instanceof Integer) {
            if (this.dict_V.containsValue(key)) {
                return ((BiHashMap)this.dict_V).getKey(key);
            }
        } else {
            if (this.dict_V.containsKey(key))
                return this.getDict_V().get(key);
        }
        return null;
    }

    @Override
    public List<Integer> getEdge(K sid) {
        return this.targets.get(this.dict_V.get(sid));
    }

    @Override
    public void clear() {
        this.targets.clear();
        this.targets = null;
        this.dict_V.clear();
        this.dict_V = null;
    }

    public Map<K, Integer> getDict_V() {
        return dict_V;
    }

    public List<List<Integer> > getTargets() {
        return this.targets;
    }
}