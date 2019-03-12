package com.sisyphsu.algorithm;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author sulin
 * @since 2019-03-12 11:03:56
 */
public class QuickSortTest {

    @Test
    public void test() {
        int[] arr = new int[]{4, 2, 5, 7, 7, 3, 3, 3, 9, 6};

        QuickSort.sort(arr, 0, arr.length - 1);

        System.out.println(Arrays.toString(arr));
    }

}
