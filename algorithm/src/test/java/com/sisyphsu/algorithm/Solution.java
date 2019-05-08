package com.sisyphsu.algorithm;

import org.junit.Test;

/**
 * Definition for singly-linked list.
 * public class ListNode {
 * int val;
 * ListNode next;
 * ListNode(int x) { val = x; }
 * }
 */
public class Solution {

    @Test
    public void test() {
        ListNode l1 = numToList(342);
        ListNode l2 = numToList(465);
        System.out.println(addTwoNumbers(l1, l2));
    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // 计算l1和l2的长度
        int len1 = length(l1);
        int len2 = length(l2);
        // 计算结果
        ListNode result = null;
        ListNode prevNode = null;
        int upper = 0; // 进位

        while (len1 > 0 || len2 > 0) {
            int currVal;

            // 只采用l1
            if (len1 > len2) {
                currVal = l1.val;
                l1 = l1.next;
                len1--;
            }

            // 只采用l2
            else if (len1 < len2) {
                currVal = l2.val;
                l2 = l2.next;
                len2--;
            }

            // l1、l2均采用
            else {
                currVal = l1.val + l2.val + upper;
                l1 = l1.next;
                l2 = l2.next;
                len1--;
                len2--;
            }

            // 计算进位
            if (currVal >= 10) {
                upper = currVal / 10;
                currVal = currVal % 10;
            }
            ListNode node = new ListNode(currVal);
            if (result == null) {
                result = node;
            }
            if (prevNode != null) {
                prevNode.next = node;
            }
            prevNode = node;
        }
        return result;
    }

    public static int length(ListNode list) {
        int len = 0;
        for (; list != null; list = list.next) {
            len++;
        }
        return len;
    }

    public static ListNode numToList(long num) {
        ListNode result = null;
        ListNode prevNode = null;
        do {
            ListNode node = new ListNode((int) (num % 10));
            num = num / 10;
            if (prevNode != null) {
                prevNode.next = node;
            }
            if (result == null) {
                result = node;
            }
            prevNode = node;
        } while (num > 0);
        return result;
    }

    public static class ListNode {

        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }

    }

}