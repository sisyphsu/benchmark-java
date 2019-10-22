package com.sisyphsu.algorithm;

import org.junit.Test;

import java.util.Random;

/**
 * @author sulin
 * @since 2019-10-02 14:43:27
 */
public class HeapSortTest {

    @Test
    public void test() {
        Random random = new Random(System.currentTimeMillis());
        long[] arr = new long[100];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(10000);
        }
        HeapSort.headSort(arr);

        for (int i = 0; i < arr.length - 1; i++) {
            assert arr[i] <= arr[i + 1];
        }
    }

}