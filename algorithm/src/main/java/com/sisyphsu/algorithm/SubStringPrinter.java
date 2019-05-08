package com.sisyphsu.algorithm;

/**
 * 2.实现函数,给定一个字符串数组,求该数组的连续非空子集，分別打印出来各子集，
 * 举例数组为[abc]，输出[a],[b],[c],[ab],[bc],[abc]
 *
 * @author sulin
 * @since 2019-03-26 19:26:54
 */
public class SubStringPrinter {

    public static void main(String[] args) {
        printConSubString("abcde");
    }

    public static void printConSubString(String str) {
        if (str == null) {
            return;
        }
        for (int sublen = 1; sublen <= str.length(); sublen++) {
            for (int offset = 0; offset <= str.length() - sublen; offset++) {
                System.out.println(str.substring(offset, offset + sublen));
            }
        }
    }

    // 顺序不对
    @Deprecated
    public static void _printConSubString(String str) {
        char prefix = str.charAt(0);
        for (int i = 0; i < str.length(); i++) {
            System.out.print(prefix);
            System.out.print(str.substring(1, i + 1));
            System.out.println();
        }
        if (str.length() > 1) {
            _printConSubString(str.substring(1));
        }
    }

}
