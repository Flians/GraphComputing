package com.antfin.arc.arch.message.graph;

import com.antfin.arc.arch.message.IWritable;

/**
 * Alipay.com Inc Copyright (c) 2004-2017 All Rights Reserved.
 *
 * @author gaolun on 18/1/21.
 */
public class Vertex<K, VV> implements IWritable {

    private K id;
    private VV value;

    public Vertex() {
    }

    public Vertex(K id) {
        this.id = id;
    }

    public Vertex(K id, VV value) {
        this.id = id;
        this.value = value;
    }

    public static <K, VV> Vertex<K, VV> of(K k, VV vv) {
        return new Vertex<>(k, vv);
    }

    public K getId() {
        return id;
    }

    public void setId(K id) {
        this.id = id;
    }

    public String getKey() {
        return "v_" + getId().toString();
    }

    public VV getValue() {
        return value;
    }

    public void setValue(VV value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Vertex) {
            Vertex vertex = (Vertex) other;
            return vertex.getId().equals(this.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "(vertexId:" + id + ",value:" + value + ")";
    }

    @Override
    public byte[] getBinaryKey() {
        return getKey().getBytes();
    }

    @Override
    public byte[] getBinaryValue() {
        return SerDeHelper.object2Byte(getValue());
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public <NV> Vertex<K, NV> replaceVertexValue(NV newValue) {
        return new Vertex<>(id, newValue);
    }
}
