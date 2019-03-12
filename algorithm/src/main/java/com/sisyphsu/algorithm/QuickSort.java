package com.sisyphsu.algorithm;

/**
 * 快速排序实现
 *
 * @author sulin
 * @since 2019-03-12 10:22:29
 */
public class QuickSort {

    /**
     * 针对数组中的片对执行快速排序
     *
     * @param arr  原始数组
     * @param from 排序开始位置, 闭区间
     * @param to   排序结束位置, 闭区间
     */
    public static void sort(int[] arr, int from, int to) {
        // 取最右侧为基准值
        int low = from;
        int high = to;
        int baseVal = arr[low];

        // 分别从两端遍历数组进行分组排序, 此时arr[low]处于闲置状态
        while (low < high) {
            // 从右侧寻找比baseVal小的一个数, 找到后将它挪至low上, 之后arr[high]将处于闲置状态
            for (; high > low; high--) {
                if (arr[high] < baseVal) {
                    arr[low] = arr[high];
                    break;
                }
            }

            // 从左侧寻找比baseVal大的一个数, 找到后将它挪至high上, 之后arr[low]将再次处于闲置状态
            for (; high > low; low++) {
                if (arr[low] > baseVal) {
                    arr[high] = arr[low];
                    break;
                }
            }
        }

        // 此时low等于high, 针对low/high的两侧进行分区排序即可
        arr[low] = baseVal;
        if (low - from > 1) {
            sort(arr, from, low - 1);
        }
        if (to - low > 1) {
            sort(arr, low + 1, to);
        }
    }

}
