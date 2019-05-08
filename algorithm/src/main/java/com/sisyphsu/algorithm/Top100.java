package com.sisyphsu.algorithm;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

/**
 * 3.文件系统中按逗号分割保存了1亿个正整数(一行10个数，1000万行)，找出其中最大的100个数
 *
 * @author sulin
 * @since 2019-03-26 19:40:04
 */
public class Top100 {

    public static void main(String[] args) {
        try {
            int[] topN = loadTopN("/Users/sulin/num.data", 5);
            for (int i : topN) {
                System.out.println(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] loadTopN(String filename, int num) throws IOException {
        int[] result = new int[num];
        FileInputStream fis = new FileInputStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null || line.length() == 0) {
                    break;
                }
                for (String item : line.split(",")) {
                    try {
                        int val = Integer.valueOf(item.trim()); // 认为
                        if (result[0] < val) {
                            result[0] = val;
                            Arrays.sort(result);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        } finally {
            reader.close();
            fis.close();
        }
        return result;
    }

}
