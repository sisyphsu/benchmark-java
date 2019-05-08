package com.sisyphsu.algorithm;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1.实现两个线程，使之交替打印1-100,如：两个线程分别为：Printer1和Printer2,
 * 最后输出结果为： Printer1 — 1 Printer2 一 2 Printer1 一 3 Printer2 一 4
 *
 * @author sulin
 * @since 2019-03-26 19:17:15
 */
public class ConcurrentPrint {

    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);
        // 启动printer1线程
        new Thread(() -> {
            while (counter.get() < 100) {
                System.out.println("Printer1 - " + counter.incrementAndGet());
                semaphore2.release(1);
                try {
                    semaphore1.acquire(1);
                } catch (InterruptedException ignored) {
                }
            }
            latch.countDown();
        }).start();

        // 启动printer2线程
        new Thread(() -> {
            while (counter.get() < 100) {
                try {
                    semaphore2.acquire(1);
                } catch (InterruptedException ignored) {
                }
                System.out.println("Printer2 - " + counter.incrementAndGet());
                semaphore1.release(1);
            }
            latch.countDown();
        }).start();

        latch.await();
    }

}
