package com.sisyphsu.algorithm;

import org.junit.Test;

/**
 * @author sulin
 * @since 2019-03-30 09:31:31
 */
public class EqualTest {

    @Test
    public void test() {
        Integer a = 128;
        Integer b = 128;
        if (a == b) {
            System.out.print("Integer Compare Integer : ");
            System.out.println("Y");
        } else {
            System.out.print("Integer Compare Integer : ");
            System.out.println("N");
        }
        Integer m = 127;
        Integer n = 127;
        if (m == n) {
            System.out.print("int Compare int : ");
            System.out.println(" Y ");
        } else {
            System.out.print("int Compare int : ");
            System.out.println(" N ");
        }

        Integer num1 = 127;
        Integer num2 = new Integer(127);
        System.out.println(num1 == num2);
    }

    @Test
    public void testString() {
        String s1 = "go" + "od";
        System.out.println(s1.intern() == s1);

        String s11 = "good";
        System.out.println(s1 == s11);

        String s12 = new String("good");
        System.out.println(s11 == s12);

        String s2 = "ja" + "va";
        System.out.println(s2.intern() == s2);
    }

    @Test
    public void testChar() {
        String data = "你好";
        System.out.println(data.charAt(0));
        System.out.println(data.toCharArray()[0]);
    }

}
