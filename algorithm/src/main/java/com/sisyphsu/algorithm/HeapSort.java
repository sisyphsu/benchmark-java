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
    public static void headSort(long[] list) {
        //构造初始堆, 从第一个非叶子节点开始调整, 左右孩子节点中较大的交换到父节点中
        for (int i = (list.length) / 2 - 1; i >= 0; i--) {
            headAdjust(list, i, list.length);
        }
        //排序, 将最大的节点放在堆尾, 然后从根节点重新调整
        for (int i = list.length - 1; i >= 1; i--) {
            long temp = list[0];
            list[0] = list[i];
            list[i] = temp;
            headAdjust(list, 0, i);
        }
    }

    private static void headAdjust(long[] list, int root, int last) {
        final long rootVal = list[root];
        int parent = root;
        int child1, child2;
        while (true) {
            child1 = 2 * parent + 1;
            child2 = child1 + 1;
            if (child1 >= last) {
                break;
            }
            if (child2 < last && list[child2] > list[child1]) {
                child1 = child2; // choice the bigger child
            }
            if (list[child1] <= rootVal) {
                break;
            }
            list[parent] = list[child1];
            parent = child1;
        }
        list[parent] = rootVal;
    }

}
