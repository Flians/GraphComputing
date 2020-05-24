package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.antfin.util.BiHashMap;
import com.antfin.util.GraphHelper;

import java.util.*;

/**
 * @param <K>
 * @param <VV>
 * @param <EV>
 * @author Flians
 * @Description: The graph is described by Compressed Sparse Row (CSR).
 *               When the vertex is assigned to a edge, it is saved into Map dict_V<vertex_id, value>.
 *               For a Vertex with id = sid, dict_V[sid] is the index of its edges in targets.
 *               If i = dict_V[sid], the j-th vertex connected by this vertex has the following relationship:
 *                  targets[i][j] = dict_V[tid] - dict_V[sid]
 * @Title: Graph_CSR_GC
 * @ProjectName graphRE
 * @date 2020/5/18 16:13
 */
public class Graph_CSR_GC_BFS_Degree<K, VV, EV> extends Graph<K, VV, EV>{
    private Map<K, Integer> dict_V;
    // store all targets. For the vertex with id = sid, i = dict_V[sid] and targets[i][j] = dict_V[tid] - dict_V[sid]
    private List<List<Integer> > targets;

    public Graph_CSR_GC_BFS_Degree() {
        this.dict_V = new BiHashMap<>();
        this.targets = new LinkedList<>();
    }

    public Graph_CSR_GC_BFS_Degree(List vg, boolean flag) {
        this();
        if (flag) {
            ((List<Vertex<K, VV>>) vg).forEach(this::addVertex);
        } else {
            ((List<Edge<K, EV>>) vg).forEach(this::addEdge);
        }
    }

    public Graph_CSR_GC_BFS_Degree(List<Vertex<K, VV>> vertices, List<Edge<K, EV>> edges) {
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
        // the source vertex of edge has no edges.
        if (this.dict_V.get(edge.getSrcId()) >= this.targets.size()) {
            // There are some wrong due to all targets need to be changed, O(m)
            this.dict_V.put((K) ((BiHashMap)this.dict_V).getKey(this.targets.size()), this.dict_V.get(edge.getSrcId()));
            this.dict_V.put(edge.getSrcId(), this.targets.size());
            List<Integer> item = new ArrayList<>();
            item.add(this.dict_V.get(edge.getTargetId()) - this.dict_V.get(edge.getSrcId()));
            this.targets.add(item);
        } else {
            // the source vertex of edge has other edges.
            this.targets.get(this.dict_V.get(edge.getSrcId())).add(this.dict_V.get(edge.getTargetId()) - this.dict_V.get(edge.getSrcId()));
        }
    }

    @Override
    public void clear() {
        this.dict_V.clear();
        this.dict_V = null;
        this.targets.clear();
        this.targets = null;
    }

    public int reorder_BFS_Degree() {
        int maxGap = 0;
        // BFS
        int[] id_new = new int[this.dict_V.size()];
        for (int i=0; i<id_new.length; ++i) id_new[i] = -1;
        int cur_idx = 0;
        Queue<Integer> Vs = new LinkedList<>();
        for (Map.Entry<K, Integer> item:this.dict_V.entrySet()) {
            if (id_new[item.getValue()] == -1) {
                id_new[item.getValue()] = cur_idx++;
                Vs.add(item.getValue());
                while (!Vs.isEmpty()) {
                    // Vertex has output edges
                    if (Vs.peek() < this.targets.size()) {
                        for (Integer tar : this.targets.get(Vs.peek()) ) {
                            Integer tarInt = tar + Vs.peek();
                            if (id_new[tarInt] == -1) {
                                id_new[tarInt] = cur_idx++;
                                Vs.add(tarInt);
                            }
                        }
                    }
                    Vs.poll();
                }
            }
        }
        Vs = null;

        // Degree
        List<Integer> id_V = new ArrayList<>(this.dict_V.size());
        for (int i=0; i<this.dict_V.size(); ++i)
            id_V.add(i);
        Collections.sort(id_V, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                Integer d1=0, d2=0;
                if (targets.size() > o1)
                    d1 = targets.get(o1).size();
                if (targets.size() > o2)
                    d2 = targets.get(o2).size();
                if (d1==d2) {
                    return id_new[o1] < id_new[o2]?-1:1;
                } else {
                    return d1 < d2 ? 1 : -1;
                }
            }
        });

        for (K key:this.dict_V.keySet()){
            this.dict_V.put(key, id_V.get(this.dict_V.get(key)));
        }

        for (int sid=0; sid < this.targets.size(); sid++) {
            // sid <--> id_new[sid]
            int sid_now = id_V.get(sid);
            GraphHelper.swap(sid, sid_now, this.targets);
            maxGap = Math.max(Math.abs(maxGap), Math.abs(this.adjustTarget(sid, sid_now, this.targets.get(sid_now))));
            this.adjustTarget(sid_now, sid, this.targets.get(sid));
        }
        id_V.clear();
        id_V = null;
        return maxGap;
    }

    private int adjustTarget(int sid_old, int sid_now, List<Integer> tars){
        int maxGap = 0;
        for (int i=0; i < tars.size(); ++i){
            int oldT = tars.get(i) + sid_old;
            this.targets.get(sid_now).set(i, oldT-sid_now);
            maxGap = Math.max(Math.abs(maxGap), Math.abs(this.targets.get(sid_now).get(i)));
        }
        return maxGap;
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
        if (this.dict_V.get(sid) < this.targets.size())
            return this.getTargets().get(this.dict_V.get(sid));
        return null;
    }

    public Map<K, Integer> getDict_V() {
        return this.dict_V;
    }

    public List<List<Integer>> getTargets() {
        return this.targets;
    }
}
