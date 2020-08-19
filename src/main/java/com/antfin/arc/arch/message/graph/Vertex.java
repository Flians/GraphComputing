package com.antfin.arc.arch.message.graph;

import com.alipay.kepler.util.SerDeHelper;
import com.antfin.arc.arch.message.IWritable;

public class Vertex<K, VV> implements IWritable {

    private K id;

    public Vertex() {
    }

    public Vertex(K id) {
        this.id = id;
    }

    public Vertex(K id, VV value) {
        this.id = id;
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
        return null;
    }

    public void setValue(VV value) {
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
        return "(vertexId:" + id + ",value:)";
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
        return false;
    }

    public <NV> Vertex<K, NV> replaceVertexValue(NV newValue) {
        return new Vertex<>(id, newValue);
    }
}
