package com.antfin.arch.cstore.persistence.table.utils;

import com.antfin.arch.cstore.data.Pair;
import com.antfin.arch.cstore.persistence.fs.IReadableFile;
import com.antfin.arch.cstore.persistence.fs.IWritableFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Encoding {

    public static byte[] encodeVarintU32(int value) {
        if (value < (128)) {    // 1 << 7 = 128
            byte[] res = new byte[1];
            res[0] = (byte) value;
            return res;
        } else if (value < (16384)) {    // 1 << 14 = 16384
            byte[] res = new byte[2];
            res[0] = (byte) (value | 128);
            res[1] = (byte) (value >> 7);
            return res;
        } else if (value < (2097152)) {    // 1 << 21 = 2097152
            byte[] res = new byte[3];
            res[0] = (byte) (value | 128);
            res[1] = (byte) ((value >> 7) | 128);
            res[2] = (byte) (value >> 14);
            return res;
        } else if (value < (268435456)) {    // 1 << 28 = 268435456
            byte[] res = new byte[4];
            res[0] = (byte) (value | 128);
            res[1] = (byte) ((value >> 7) | 128);
            res[2] = (byte) ((value >> 14) | 128);
            res[3] = (byte) (value >> 21);
            return res;
        } else {
            byte[] res = new byte[5];
            res[0] = (byte) (value | 128);
            res[1] = (byte) ((value >> 7) | 128);
            res[2] = (byte) ((value >> 14) | 128);
            res[3] = (byte) ((value >> 21) | 128);
            res[4] = (byte) (value >> 28);
            return res;
        }
    }

    public static void encodeVarintU32(int value, IWritableFile writableFile) throws IOException {
        if (value < (128)) {
            byte[] res = new byte[1];
            res[0] = (byte) value;
            writableFile.write(res);
        } else if (value < (16384)) {
            byte[] res = new byte[2];
            res[0] = (byte) (value | 128);
            res[1] = (byte) (value >> 7);
            writableFile.write(res);
        } else if (value < (2097152)) {
            byte[] res = new byte[3];
            res[0] = (byte) (value | 128);
            res[1] = (byte) ((value >> 7) | 128);
            res[2] = (byte) (value >> 14);
            writableFile.write(res);
        } else if (value < (268435456)) {
            byte[] res = new byte[4];
            res[0] = (byte) (value | 128);
            res[1] = (byte) ((value >> 7) | 128);
            res[2] = (byte) ((value >> 14) | 128);
            res[3] = (byte) (value >> 21);
            writableFile.write(res);
        } else {
            byte[] res = new byte[5];
            res[0] = (byte) (value | 128);
            res[1] = (byte) ((value >> 7) | 128);
            res[2] = (byte) ((value >> 14) | 128);
            res[3] = (byte) ((value >> 21) | 128);
            res[4] = (byte) (value >> 28);
            writableFile.write(res);
        }
    }

    public static int decodeVarintU32(final byte[] value) {
        int length = value.length;
        int decodedInt = 0;
        for (int i = 0; i < length; i++) {
            decodedInt += ((value[i] & 127) << (i * 7));
            int hasNext = value[i] & 128;
            if (hasNext == 0) {
                break;
            }
        }
        return decodedInt;
    }

    public static Pair<Integer, Integer> decodeNextVarintU32(final byte[] value, final int offset) {
        int length = value.length;
        int decodedInt = 0;
        int i = 0;
        int valueIndex = offset;
        while (i < length) {
            decodedInt += ((value[valueIndex] & 127) << (i++ * 7));
            int hasNext = value[valueIndex] & 128;
            valueIndex++;
            if (hasNext == 0) {
                break;
            }
        }
        return Pair.of(decodedInt, valueIndex - offset);
    }

    public static List<Integer> decodeVarintU32(final byte[] value, int offset, int desiredCount) {
        List<Integer> results = new ArrayList<>();
        int valueIndex = offset;
        while (desiredCount-- > 0) {
            int decodedInt = 0;
            int j = 0;
            while (true) {
                decodedInt += ((value[valueIndex] & 127) << (j++ * 7));
                int hasNext = value[valueIndex] & 128;
                valueIndex++;
                if (hasNext == 0) {
                    results.add(decodedInt);
                    break;
                }
            }
        }
        return results;
    }

    public static int decodeVarintU32(IReadableFile readableFile) throws IOException {
        int decodedInt = 0;
        int decodedByteCount = 0;

        while (true) {
            byte[] nextByte = new byte[1];
            int res = readableFile.read(nextByte);
            if (res == -1) {
                return -1;
            }
            decodedInt += ((nextByte[0] & 127) << (decodedByteCount * 7));
            decodedByteCount++;
            int hasNext = nextByte[0] & 128;
            if (hasNext == 0) {
                return decodedInt;
            }
        }
    }
}