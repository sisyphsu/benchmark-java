package com.sisyphsu.algorithm;

/**
 * 堆排序算法
 *
 * @author sulin
 * @since 2019-03-26 13:56:02
 */
public class HeapSort {

    /**
     * 堆排序
     */
    public static void headSort(int[] list) {
        //构造初始堆, 从第一个非叶子节点开始调整, 左右孩子节点中较大的交换到父节点中
        for (int i = (list.length) / 2 - 1; i >= 0; i--) {
            headAdjust(list, list.length, i);
        }
        //排序, 将最大的节点放在堆尾, 然后从根节点重新调整
        for (int i = list.length - 1; i >= 1; i--) {
            int temp = list[0];
            list[0] = list[i];
            list[i] = temp;
            headAdjust(list, i, 0);
        }
    }

    private static void headAdjust(int[] list, int len, int i) {
        int k = i, root = list[i], index = 2 * k + 1;
        while (index < len) {
            if (index + 1 < len) {
                if (list[index] < list[index + 1]) {
                    index = index + 1;
                }
            }
            if (list[index] > root) {
                list[k] = list[index];
                k = index;
                index = 2 * k + 1;
            } else {
                break;
            }
        }
        list[k] = root;
    }

}