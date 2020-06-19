package com.antfin.util;

import com.alipay.kepler.util.SerDeHelper;
import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arch.cstore.benchmark.RandomWord;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class GraphHelper {

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
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        byte[] res = SerDeHelper.object2Byte(object);
        FileOutputStream opStream = new FileOutputStream(file);
        opStream.write(res);
        opStream.flush();
        opStream.close();
    }

    public static List<Edge<String, String>> loadEdges(String path){
        List<Edge<String, String>> edges = new ArrayList<>();
        readKVFile(path).forEach(pair->{
            edges.add(new Edge<>(pair.getKey(), pair.getValue(), RandomWord.getWords(100)));
        });
        return edges;
    }

    public static List<Pair<String, String>> readKVFile(String path){
        File file = new File(path);
        if (!file.exists()) {
            System.err.println(path + " is not exist!");
        }
        List<Pair<String, String>> pairs = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), Charset.forName("utf-8"))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0)
                    continue;
                String[] vid = line.split(" ");
                if (vid.length != 2)
                {
                    System.err.println(line + " must include source and sink!");
                }
                pairs.add(new Pair<>(vid[0], vid[1]));
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return pairs;
    }
}
