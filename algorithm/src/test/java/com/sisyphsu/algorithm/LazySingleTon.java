package com.sisyphsu.algorithm;

import org.junit.Test;

/**
 * @author sulin
 * @since 2019-03-26 16:36:41
 */
public class LazySingleTon {

    public static class Bean {
        private Bean() {
            System.out.println("init bean");
        }

        public static Bean getInstance() {
            System.out.println("getInstance");
            return Singleton.INSTANCE;
        }

        private static class Singleton {
            static final Bean INSTANCE = new Bean();
        }
    }

    @Test
    public void test() {
        Bean.getInstance();
        System.out.println(this.m1());
    }

    public synchronized long m1() {
        return this.m2();
    }

    public synchronized long m2() {
        return System.currentTimeMillis();
    }

}
