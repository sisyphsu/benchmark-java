package com.github.sisyphsu.benchmark.json;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * @author sulin
 * @since 2019-09-30 12:05:01
 */
public class FKTest {

    @Test
    public void test() {
        Thread mainThread = Thread.currentThread();
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        System.out.println("main thread: " + Thread.currentThread().getId());

        list.parallelStream().forEach(id -> {
            Thread thread = Thread.currentThread();
            if (thread instanceof ForkJoinWorkerThread) {
                Thread[] threads = getThreadGroupMembers();
                assert threads[0] == mainThread; // 定位到主线程
            } else {
                assert thread == mainThread; // 定位到主线程
            }
        });
    }

    public static Thread[] getThreadGroupMembers() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        try {
            Field field = ThreadGroup.class.getDeclaredField("threads");
            field.setAccessible(true);
            return (Thread[]) field.get(threadGroup);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
