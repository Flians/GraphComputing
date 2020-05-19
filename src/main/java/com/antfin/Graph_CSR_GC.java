package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param <K>
 * @param <VV>
 * @param <EV>
 * @author Flians
 * @Description: ${description}
 * @Title: Graph_CSR_GC
 * @ProjectName graphRE
 * @date 2020/5/18 16:13
 */
public class Graph_CSR_GC<K, VV, EV> extends Graph<K, VV, EV>{
    private Map<K, Integer> dict_V;
    // store all targets. For i-th vertex in vertices, i = dict_V[sid] and targets[i][j] = dict_V[tid] - dict_V[sid]
    private List<List<Short> > targets;
    private List<Integer> csr;

    public Graph_CSR_GC() {
        this.dict_V = new HashMap<>();
        this.targets = new ArrayList<>();
        this.csr = new ArrayList<>();
    }

    public Graph_CSR_GC(List vg, boolean flag) {
        this();
        if (flag) {
            ((List<Vertex<K, VV>>) vg).forEach(this::addVertex);
        } else {
            ((List<Edge<K, EV>>) vg).forEach(this::addEdge);
        }
    }

    public Graph_CSR_GC(List<Vertex<K, VV>> vertices, List<Edge<K, EV>> edges) {
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
            this.csr.add(-1);
        }
    }

    @Override
    public void addEdge(Edge<K, EV> edge) {
        this.addVertex(edge.getSrcId());
        this.addVertex(edge.getTargetId());
        // the source vertex of edge has no edges.
        if (this.csr.get(this.dict_V.get(edge.getSrcId())) == -1) {
            this.csr.set(this.dict_V.get(edge.getSrcId()), this.targets.size());
            List<Short> item = new ArrayList<>();
            item.add((short) (this.dict_V.get(edge.getTargetId()) - this.dict_V.get(edge.getSrcId())));
            this.targets.add(item);
        } else {
            // the source vertex of edge has other edges.
            this.targets.get(this.csr.get(this.dict_V.get(edge.getSrcId()))).add((short) (this.dict_V.get(edge.getTargetId()) - this.dict_V.get(edge.getSrcId())));
        }
    }

    @Override
    public Vertex<K, VV> getVertex(K id) {
        return null;
    }

    @Override
    public List<Edge<K, EV>> getEdge(K sid) {
        return null;
    }

    public Map<K, Integer> getDict_V() {
        return this.dict_V;
    }

    public List<List<Short>> getTargets() {
        return this.targets;
    }

    public List<Integer> getCsr() {
        return this.csr;
    }
}
