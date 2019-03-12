package com.sisyphsu.algorithm;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * @author sulin
 * @since 2019-03-12 11:03:56
 */
public class QuickSortTest {

    @Test
    public void test() {
        int[] arr = new int[]{4, 2, 5, 7, 7, 3, 3, 4, 9, 6};

        QuickSort.sort(arr, 0, arr.length - 1);

        System.out.println(Arrays.toString(arr));
    }

    @Test
    public void testIsSort() {
        assert QuickSort.isSorted(new int[]{1, 1, 1, 2, 3, 4, 5, 5, 5, 7, 9});
        assert !QuickSort.isSorted(new int[]{1, 1, 2, 1, 3, 4, 5, 5, 5, 7, 9});
    }

    @Test
    public void benchmark() {
        // 准备数据
        int[] arr = new int[1000];
        Random rand = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Math.abs(rand.nextInt()) % 750 + 1;
        }

        // 执行排序
        QuickSort.sort(arr, 0, arr.length - 1);

        // 检查结果
        assert QuickSort.isSorted(arr);
    }

}
