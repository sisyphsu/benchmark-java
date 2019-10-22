package com.github.sisyphsu.benchmark.lang;

import com.github.sisyphsu.benchmark.Runner;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark                 Mode  Cnt  Score   Error  Units
 * ThreadLocalBenchmark.get  avgt    9  1.606 ± 0.013  ns/op
 * ThreadLocalBenchmark.set  avgt    9  3.589 ± 0.088  ns/op
 *
 * @author sulin
 * @since 2019-10-22 20:15:12
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ThreadLocalBenchmark {

    private static final ThreadLocal<Long> LOCAL = new ThreadLocal<>();

//    static {
//        int count = 500;
//        CountDownLatch latch = new CountDownLatch(count);
//        for (int i = 0; i < count; i++) {
//            Thread thread = new Thread(() -> {
//                LOCAL.set(System.currentTimeMillis());
//                latch.countDown();
//                try {
//                    Thread.sleep(10000000);
//                } catch (InterruptedException ignored) {
//                }
//            });
//            thread.setDaemon(true);
//            thread.start();
//        }
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Benchmark
    public void set() {
        LOCAL.set(1L);
    }

    @Benchmark
    public void get() {
        LOCAL.get();
    }

    public static void main(String[] args) {
        Runner.run(ThreadLocalBenchmark.class);
    }

}
