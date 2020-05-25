package com.antfin.util;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.graph.Graph;
import com.antfin.graph.refObj.Graph_CSR_EL_List;
import com.antfin.graph.refObj.Graph_CSR_EL_Map;
import com.antfin.graph.refObj.Graph_CSR_GC;
import com.antfin.graph.refObj.Graph_CSR_GC_STD;

import java.util.List;
import java.util.Map;

public class GraphVerify {
    public static int verify(String key, Map<String, Map<String, Boolean>> originGraph, Graph nowGraph) {
        int numE = 0;
        if (originGraph.containsKey(key)) {
            numE = ((List) nowGraph.getEdge(key)).size();
            if (nowGraph instanceof Graph_CSR_EL_Map || nowGraph instanceof Graph_CSR_EL_List || nowGraph instanceof Graph_CSR_GC || nowGraph instanceof Graph_CSR_GC_STD) {
                int sid = (int) nowGraph.getVertex(key);
                for (Integer gap : ((List<Integer>) nowGraph.getEdge(key))) {
                    if (!(nowGraph instanceof Graph_CSR_EL_Map) && !(nowGraph instanceof Graph_CSR_EL_List)) {
                        gap += sid;
                    }
                    if (!originGraph.get(key).containsKey(nowGraph.getVertex(gap))) {
                        System.out.println("<" + key + "," + nowGraph.getVertex(gap) + "> is not existed!");
                    }
                    if (nowGraph instanceof Graph_CSR_GC_STD)
                        sid = gap;
                }
            } else {
                ((List<Edge<String, String>>) nowGraph.getEdge(key)).forEach(e -> {
                    if (!originGraph.get(key).containsKey(e.getTargetId())) {
                        System.out.println("<" + key + "," + e.getTargetId() + "> is not existed!");
                    }
                });
            }
        } else {
            // System.out.println(key + " has no output edges!");
        }
        return numE;
    }
}
