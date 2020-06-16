package com.antfin.util;

import com.alipay.kepler.util.SerDeHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphHelper<K, VV, EV> {

    public static void swap(int x, int y, List ll) {
        Object ob = ll.get(x);
        ll.set(x, ll.get(y));
        ll.set(y, ob);
    }

    /**
     * int到byte[]  from high to low
     * byte[0] = 0 represent i >= 0
     *
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
        result[0] = (byte) (flag ? 1 : 0);
        return result;
    }

    /**
     * byte[] changed into int
     *
     * @return int
     */
    public static int byteArrayToInt(byte[] bytes) {
        int length = bytes.length;
        int value = 0;
        for (int i = 1; i < length; i++) {
            int shift = (length - 1 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return bytes[0] == 0 ? value : -value;
    }

    public static long[] countEdges(List<List<Integer>> targets) {
        long nums[] = new long[3];
        for (Object edges : targets) {
            nums[0] += ((List<Integer>) edges).size();
            for (Integer edge : ((List<Integer>) edges)) {
                nums[1] += GraphHelper.intToByteArray(edge).length;
            }
        }
        nums[2] = nums[0] * 16 - nums[1] * 4;
        return nums;
    }

    public static long[] countEdgesBytes(List<List<byte[]>> targets) {
        long nums[] = new long[3];
        for (Object edges : targets) {
            nums[0] += ((List<byte[]>) edges).size();
            for (byte[] edge : ((List<byte[]>) edges)) {
                nums[1] += edge.length;
            }
        }
        nums[2] = nums[0] * 16 - nums[1] * 4;
        return nums;
    }

    public static double convert(double value) {
        long l1 = Math.round(value * 100);
        double ret = l1 / 100.0;
        return ret;
    }

    public static Object loadObject(File file) throws IOException {
        byte[] fileContent = new byte[0];
        fileContent = Files.readAllBytes(file.toPath());
        return SerDeHelper.byte2Object(fileContent);
    }

    public static void writeObject(Object object, File file) throws IOException {
        byte[] res = SerDeHelper.object2Byte(object);
        FileOutputStream opStream = new FileOutputStream(file);
        opStream.write(res);
        opStream.flush();
        opStream.close();
    }

    public static <K> List<Map<K, List<K>>> partitionDict(Map<K, List<K>> vertices, int workers) {
        int batchSize = (vertices.size() - 1) / workers + 1;
        List<Map<K, List<K>>> partList = new ArrayList<>();
        Map<K, List<K>> part = new HashMap<>();
        int i = 0;
        for (Map.Entry<K, List<K>> entry : vertices.entrySet()) {
            K mapKey = entry.getKey();
            List<K> mapValue = entry.getValue();
            part.put(entry.getKey(), entry.getValue());
            ++i;
            if (i % batchSize == 0) {
                partList.add(part);
                part = new HashMap<>();
            }
        }
        if (!part.isEmpty()) {
            partList.add(part);
        }
        return partList;
    }

    /**
     * @return the distance between 2 time series
     */
    public static double dtw(List<Object> a, List<Object> b, int radius, DistanceFunction disFun) {
        double result = 0.0;
        if (!a.isEmpty() && !b.isEmpty()) {
            int la = a.size(), lb = b.size();
            double[][] distance = new double[la][lb];
            double[][] dp = new double[la + 1][lb + 1];
            for (int i = 0; i < la; ++i) {
                dp[i + 1][0] = Double.MAX_VALUE;
                for (int j = 0; j < lb; ++j) {
                    distance[i][j] = disFun.calcDistance(a.get(i), b.get(j));
                }
            }
            for (int j = 1; j <= lb; ++j) {
                dp[0][j] = Double.MAX_VALUE;
            }
            for (int i = 1; i <= la; ++i) {
                for (int j = 1; j <= lb; ++j) {
                    dp[i][j] = distance[i - 1][j - 1] + Math.min(
                        Math.min(dp[i - 1][j], dp[i][j - 1]),
                        dp[i - 1][j - 1] + distance[i - 1][j - 1]
                    );
                }
            }
            result = dp[la][lb];
        }
        return result;
    }

    /**
     * @param radius size of neighborhood when expanding the path
     * @param disFun The method for calculating the distance between a[i] and b[j]
     * @return the approximate distance between 2 time series with O(N) time and memory complexity
     */
    public static double fastDtw(List<Object> a, List<Object> b, int radius, DistanceFunction disFun) {
        double result = 0.0;
        if (!a.isEmpty() && !b.isEmpty()) {
            if (a.get(0) instanceof Double) {

            } else {

            }
        }
        return result;
    }
}
