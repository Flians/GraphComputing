package com.antfin.arch.cstore.benchmark;

import com.alipay.kepler.manage.accessor.impl.graph.GraphRocksDBOptions;
import com.alipay.kepler.util.SerDeHelper;
import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import org.rocksdb.Options;
import org.rocksdb.RocksIterator;
import org.rocksdb.TtlDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphGenerator {
    private static final String UidPREFIX = "2088";
    public static String randomWord = RandomWord.getWords(100);
    private Random random = new Random();

    private String[] getUids(int size) {
        String[] uids = new String[size];
        for (int i = 0; i < size; i++) {
            uids[i] = UidPREFIX + RandomWord.getNumbers(12);
        }
        return uids;
    }

    public List<Edge<String, String>> getEdges(int size, int outEdgesNum) {
        List<Edge<String, String>> edges = new ArrayList<>(size);
        String[] uids = getUids(size / outEdgesNum);

        for (int i = 0; i < size; i++) {
            String a = uids[random.nextInt(uids.length)];
            String b = uids[random.nextInt(uids.length)];
            if (a.compareTo(b) > 0) {
                String c = b;
                b = a;
                a = c;
            }
            edges.add(new Edge<>(a, b, RandomWord.getWords(100)));
        }
        return edges;
    }

    public List<Edge<Long, String>> getEdges2(int size) {
        List<Edge<Long, String>> edges = new ArrayList<>(size);
        String[] uids = getUids(size);

        for (int i = 0; i < size; i++) {
            String a = uids[random.nextInt(uids.length)];
            String b = uids[random.nextInt(uids.length)];
            if (a.compareTo(b) > 0) {
                String c = b;
                b = a;
                a = c;
            }
            edges.add(new Edge<>(Long.parseLong(a), Long.parseLong(b), randomWord));
        }
        return edges;
    }

    public List<Vertex<String, String>> getVertices(int size) {
        List<Vertex<String, String>> vertices = new ArrayList<>(size);
        String[] uids = getUids(size / 20);

        for (int i = 0; i < size; i++) {
            vertices.add(new Vertex<>(uids[random.nextInt(uids.length)], RandomWord.getWords(100)));
        }
        return vertices;
    }

    public List<Edge<String, String>> getEdgesFromRDB(String[] paths, int edgeSize) {
        Options options = new GraphRocksDBOptions().getOptions();
        List<Edge<String, String>> edges = new ArrayList<>(edgeSize);
        int count = 0;
        for (String path : paths) {
            try (TtlDB tmp = TtlDB.open(options, path, 31536000, false)) {
                RocksIterator rocksIterator = tmp.newIterator();
                for (rocksIterator.seekToFirst(); rocksIterator.isValid(); rocksIterator.next()) {
                    byte[] keyBytes = rocksIterator.key();
                    byte[] value = rocksIterator.value();
                    String key = new String(keyBytes);
                    Edge<String, String> edge = Edge.of(key.split(Edge.DELIMITER)[0].substring(2),
                            new String(keyBytes),
                            SerDeHelper.byte2Object(value));
                    edges.add(edge);
                    if (++count == edgeSize) {
                        break;
                    }
                }
                rocksIterator.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (count >= edgeSize) {
                break;
            }
        }
        return edges;
    }

    public enum DataInjectType {
        RANDOM, ROCKSDB;
    }
}
