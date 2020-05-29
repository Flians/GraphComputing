package com.antfin.util;

import java.util.List;

public class GraphHelper<VV> {

    public static void swap(int x, int y, List ll) {
        Object ob = ll.get(x);
        ll.set(x, ll.get(y));
        ll.set(y, ob);
    }

    /**
     * int到byte[]  from high to low
     * byte[0] = 0 represent i >= 0
     * @param i
     * @return byte[]
     */
    public static byte[] intToByteArray(int i) {
        boolean flag = false;
        if (i < 0) {
            flag = true;
            i = -i;
        }
        byte[] result;
        if ((i >> 24) != 0) {
            result = new byte[5];
            result[1] = (byte) ((i >> 24) & 0xFF);
            result[2] = (byte) ((i >> 16) & 0xFF);
            result[3] = (byte) ((i >> 8) & 0xFF);
            result[4] = (byte) (i & 0xFF);
        } else if ((i >> 16) != 0) {
            result = new byte[4];
            result[1] = (byte) ((i >> 16) & 0xFF);
            result[2] = (byte) ((i >> 8) & 0xFF);
            result[3] = (byte) (i & 0xFF);
        } else if ((i >> 8) != 0) {
            result = new byte[3];
            result[1] = (byte) ((i >> 8) & 0xFF);
            result[2] = (byte) (i & 0xFF);
        } else {
            result = new byte[2];
            result[1] = (byte) (i & 0xFF);
        }
        result[0] = (byte) (flag?1:0);
        return result;
    }

    /**
     * byte[] changed into int
     *
     * @param bytes
     * @return int
     */
    public static int byteArrayToInt(byte[] bytes) {
        int length = bytes.length;
        int value = 0;
        for (int i = 1; i < length; i++) {
            int shift = (length - 1 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return bytes[0]==0?value:-value;
    }

    public static long[] countEdges(List<List<Integer>> targets) {
        long nums[] = new long[3];
        for (Object edges:targets){
            nums[0] += ((List<Integer>)edges).size();
            for (Integer edge: ((List<Integer>)edges)) {
                nums[1] += GraphHelper.intToByteArray(edge).length;
            }
        }
        nums[2] = nums[0]*16 - nums[1]*4;
        return nums;
    }

    public static long[] countEdgesBytes(List<List<byte[]>> targets) {
        long nums[] = new long[3];
        for (Object edges:targets){
            nums[0] += ((List<byte[]>)edges).size();
            for (byte[] edge: ((List<byte[]>)edges)) {
                nums[1] += edge.length;
            }
        }
        nums[2] = nums[0]*16 - nums[1]*4;
        return nums;
    }

    public static double convert(double value){
        long l1 = Math.round(value*100);
        double ret = l1/100.0;
        return ret;
    }
}
