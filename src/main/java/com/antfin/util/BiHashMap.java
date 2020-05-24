package com.antfin.util;

import java.io.Serializable;
import java.util.*;

/**
 * @author Flians
 * @Description: A new type of Map with one-to-one mapping. Key and value are not repeated
 * @Title: Graph_CSR_GC
 * @ProjectName graphRE
 * @date 2020/5/21 10:09
 * @param <K>
 * @param <V>
 */
public class BiHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {
    private Map<K, V> kMap;
    private Map<V, K> vMap;

    public BiHashMap() {
        this.kMap = new HashMap<>();
        this.vMap = new HashMap<>();
    }

    public BiHashMap(Map<? extends K, ? extends V> map) {
        this();
        this.putAll(map);
    }

    @Override
    public V put(K k, V v) {
        if (null == k || null == v) return null;
        if (this.kMap.containsKey(k)) {
            if (v.equals(this.kMap.get(k)))
                return v;
            /*
            if (this.vMap.containsKey(v))
                this.kMap.put(this.vMap.get(v), null);
            if (this.kMap.get(k) != null)
                this.vMap.put(this.kMap.get(k), null);
             */
        }
        this.kMap.put(k, v);
        this.vMap.put(v,k);
        return v;
    }

    @Override
    public V get(Object key) {
        return this.kMap.get(key);
    }

    public K getKey(Object val) {
        return this.vMap.get(val);
    }

    @Override
    public V remove(Object key) {
        V v=null;
        if (this.kMap.containsKey(key)) {
            v = this.kMap.get(key);
            this.vMap.remove(v);
            this.kMap.remove(key);
        }
        return v;
    }

    @Override
    public void clear() {
        this.kMap.entrySet().clear();
        this.vMap.entrySet().clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.kMap.entrySet();
    }

    @Override
    public Set<K> keySet() {
        return this.kMap.keySet();
    }

    @Override
    public Set<V> values() {
        return this.vMap.keySet();
    }

    @Override
    public boolean containsKey(Object k) {
        return kMap.containsKey(k);
    }

    @Override
    public boolean containsValue(Object v) {
        return vMap.containsKey(v);
    }
}