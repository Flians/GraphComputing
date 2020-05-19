package com.antfin.arc.arch.message.graph;

import com.alipay.kepler.util.SerDeHelper;
import com.antfin.arc.arch.message.IWritable;

import java.util.Objects;

/**
 * Alipay.com Inc Copyright (c) 2004-2017 All Rights Reserved.
 *
 * @author gaolun on 18/1/21.
 */
public class Edge<K, EV> implements IWritable {

    public static final String DELIMITER = "\u0001" + "\u0008";
    private K srcId;
    private K targetId;
    private EV value;
    private long time;
    private EdgeType type = EdgeType.OUT;
    private String edgeType = "default";

    public Edge() {
    }

    public Edge(K srcId, K targetId, EV value) {
        this(srcId, targetId, value, EdgeType.OUT);
    }

    public Edge(K srcId, K targetId, EV value, EdgeType type) {
        this.srcId = srcId;
        this.targetId = targetId;
        this.value = value;
        this.type = type;
    }

    public Edge(K srcId, K targetId, EV value, long time) {
        this(srcId, targetId, value, EdgeType.OUT);
        this.time = time;
    }

    public Edge(K srcId, K targetId, EV value, long time, EdgeType type) {
        this(srcId, targetId, value, type);
        this.time = time;
    }


    public Edge(K srcId, K targetId, EV value, long time, EdgeType type, String edgeType) {
        this(srcId, targetId, value, time, type);
        this.edgeType = edgeType;
    }

    public static <K, EV> Edge of(K srcId, String edgeStr, EV value) {
        String[] values = edgeStr.split(Edge.DELIMITER);

        if (values.length == 5) {
            return new Edge<>(srcId, values[1], value, Long.valueOf(values[2]),
                Edge.EdgeType.valueOf(values[3]), values[4]);
        } else if (values.length == 4) {
            return new Edge<>(srcId, values[1], value, Long.valueOf(values[2]),
                Edge.EdgeType.valueOf(values[3]));
        } else if (values.length == 3) {
            return new Edge<>(srcId, values[1], value, Long.valueOf(values[2]));
        } else {
            return null;
        }
    }

    public static <K, EV> Edge<K, EV> of(K src, K tar, EV ev) {
        return new Edge<>(src, tar, ev);
    }

    public EdgeType getType() {
        return type;
    }

    public K getSrcId() {
        return srcId;
    }

    public void setSrcId(K srcId) {
        this.srcId = srcId;
    }

    public K getTargetId() {
        return targetId;
    }

    public void setTargetId(K targetId) {
        this.targetId = targetId;
    }

    public String getKey() {
        return String
            .format("e_%s%s%s%s%d%s%s%s%s", getSrcId(), DELIMITER, getTargetId(), DELIMITER,
                getTime(), DELIMITER, type.name(), DELIMITER, edgeType);
    }

    public EV getValue() {
        return value;
    }

    public void setValue(EV value) {
        this.value = value;
    }

    public Edge<K, EV> getReverseEdge() {
        return new Edge<>(this.getTargetId(), this.getSrcId(), this.getValue());
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcId, targetId, time, type, edgeType);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Edge) {
            Edge edge = (Edge) other;
            return Objects.equals(srcId, edge.getSrcId())
                && Objects.equals(targetId, edge.getTargetId())
                && Objects.equals(edgeType, edge.getEdgeType())
                && edge.getTime() == time
                && edge.getType() == type;
        }
        return false;
    }

    @Override
    public String toString() {
        return srcId + "#" + targetId + "#" + value;
    }

    public void setEdgeType(String edgeType) {
        this.edgeType = edgeType;
    }

    public String getEdgeType() {
        return edgeType;
    }

    @Override
    public byte[] getBinaryKey() {
        return getKey().getBytes();
    }

    @Override
    public byte[] getBinaryValue() {
        return SerDeHelper.object2Byte(getValue());
    }

    public enum EdgeType {
        IN, OUT
    }

    public Edge<K, EV> cloneWithoutSrc() {
        return new Edge<>(null, targetId, value, time, type, edgeType);
    }

    public <NV> Edge<K, NV> replaceEdgeValue(NV newValue) {
        return new Edge<>(srcId, targetId, newValue, time, type, edgeType);
    }

    public Edge<K, EV> reverse() {
        return new Edge<>(targetId, srcId, value, time, type, edgeType);
    }
}