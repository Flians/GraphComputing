/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.antfin.arch.cstore.benchmark;

import java.util.Random;

/**
 * @author xuyitian
 * @version $Id: RandomWord.java, v 0.1 2016年8月18日 下午5:01:13 xuyitian Exp $
 */
public class RandomWord {

    private static String[] words = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "g", "k", "l", "m", "n",
        "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private static String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static Random x = new Random();

    public static String getWords(int legth) {
        String word = "";
        if (legth == 0) {
            legth = x.nextInt(100);
        }
        for (int i = 0; i < legth; i++) {
            int p = x.nextInt(26);
            word = word + words[p];
        }
        return word;
    }

    public static String getNumbers(int legth) {
        String word = "";
        if (legth == 0) {
            legth = x.nextInt(100);
        }
        for (int i = 0; i < legth; i++) {
            int p = x.nextInt(10);
            word = word + numbers[p];
        }
        return word;
    }

}
