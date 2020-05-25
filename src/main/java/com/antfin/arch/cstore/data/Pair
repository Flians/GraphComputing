package com.antfin.arch.cstore.data;

/**
 * Alipay.com Inc
 * Copyright (c) 2004-2020 All Rights Reserved.
 *
 * @author wutao on 2020/4/13.
 */
public class Pair<U, V> {

    public final U f0;
    public final V f1;

    private Pair(U first, V second) {
        this.f0 = first;
        this.f1 = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;
        return f0.equals(pair.f0) && f1.equals(pair.f1);
    }

    @Override
    public int hashCode() {
        return 31 * f0.hashCode() + f1.hashCode();
    }

    @Override
    public String toString() {
        return "(" + f0 + ", " + f1 + ")";
    }

    public static <U, V> Pair<U, V> of(U a, V b) {
        return new Pair<>(a, b);
    }
}