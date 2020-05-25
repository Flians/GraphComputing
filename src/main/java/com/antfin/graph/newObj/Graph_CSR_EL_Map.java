package com.antfin.graph.newObj;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.graph.Graph;
import com.antfin.util.BiHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Flians
 * @Description: Compare the corresponding method in refObj.
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

public class Graph_CSR_EL_Map<K, VV, EV> extends Graph<K, VV, EV> {
    // record all vertices; K -> vertex.id
    private Map<K, Integer > dict_V;
    // record all edges; K -> edge.source.id
    private Map<Integer, List<Integer> > targets;

    public Graph_CSR_EL_Map() {
        this.dict_V = new BiHashMap<>();
        this.targets = new HashMap<>();
    }

    public Graph_CSR_EL_Map(List vg, boolean flag) {
        this();
        if (flag) {
            ((List<Vertex<K, VV>>) vg).forEach(this::addVertex);
        } else {
            ((List<Edge<K, EV>>)vg).stream().forEach(this::addEdge);
        }
    }

    public Graph_CSR_EL_Map(List<Vertex<K, VV>> vertices, List<Edge<K, EV>> edges) {
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
        }
    }

    @Override
    public void addEdge(Edge<K, EV> edge) {
        this.addVertex(edge.getSrcId());
        this.addVertex(edge.getTargetId());
        Integer sid= this.dict_V.get(edge.getSrcId());
        if (this.targets.containsKey(sid)) {
            this.targets.get(sid).add(new Integer(this.dict_V.get(edge.getTargetId())));
        } else {
            List<Integer > temp = new ArrayList<>();
            temp.add(new Integer(this.dict_V.get(edge.getTargetId())));
            this.targets.put(sid, temp);
        }
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

    public Map<Integer, List<Integer> > getTargets() {
        return this.targets;
    }
}