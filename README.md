# Graph Computing


## Graph representation
To save memory as much as possible, and keep find the Vertex by O(1)
### Based on Vertex and Edge
#### Edge List
+ Graph_Map_EL ——> 
Map<K, Vertex> vertices, Map<K, List<Edge>> edges.
#### CSR
+ Graph_Map_CSR --> 
List<Vertex> vertices. List<List<Edge> edges, Map<K, Integer> dictV

### Based on K,V
#### Adjacency List	
+ Graph_CSR_EL_Map -->
Map<K, Integer> dict_V; Map<Integer, List<Integer>> targets;
+ Graph_CSR_EL_List -->
Map<K, Integer> dict_V; List<List<Integer>> targets;

#### CSR
+ Graph_CSR_GC_Brother --> 
Map<K, Integer> dict_V; List<List<byte[]>> targets; List<Integer> csr; <br>
i = csr[dict_V[sid]] <br>
targets[i][0] = dict_V[tid] - dict_V[sid] <br>
targets[i][j] = SUB(dict_V[tid] - targets[i][k]) - dict_V[sid], k in [0, j) <br>
+ Graph_CSR_GC -->
Map<K, Integer> dict_V; List<List< byte[]>> targets;	


## Struc2vec
Struc2vec implemented by Java and Graph_Map_CSR


## A* search Algorithm
A* is a modification of Dijkstra’s Algorithm that is optimized for a single destination. <br>
Dijkstra’s Algorithm can find paths to all locations; <br>
A* finds paths to one location, or the closest of several locations. <br>
It prioritizes paths that seem to be leading closer to a goal. <br>