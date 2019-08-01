package com.github.sisyphsu.benchmark.lang;

import com.github.sisyphsu.benchmark.Runner;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * 测试Volatile对写操作性能的影响
 * Benchmark                                     Mode  Cnt   Score    Error   Units
 * VolatileBenchmark.normal                      avgt    9   1.354 ±  0.011   ns/op
 * VolatileBenchmark.trans                       avgt    9   1.354 ±  0.007   ns/op
 * VolatileBenchmark.volat                       avgt    9   6.438 ±  0.032   ns/op
 * <p>
 * 测试Volatile和Unsafe对数组访问的性能影响
 * Benchmark                                     Mode  Cnt   Score     Error   Units
 * VolatileBenchmark.normal                      avgt    9   0.512 ±   0.004   ns/op
 * VolatileBenchmark.volat                       avgt    9   0.255 ±   0.002   ns/op
 *
 * @author sulin
 * @since 2019-07-29 18:29:38
 */
@Slf4j
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class VolatileBenchmark {

    private static long counter1 = 0L;
    private static volatile long counter2 = 0L;

    private static Object[] arr1 = new Object[100];
    private static volatile Object[] arr2 = new Object[1000];

    static {
        for (int i = 0; i < 100; i++) {
            Object o = new Object();
            arr1[i] = o;
            arr2[i] = o;
        }
    }

    @Benchmark
    public void normal() {
//        counter1 += 1;
//        Object o = arr1[3];
        long l = counter1;
    }

    @Benchmark
    public void volat() {
//        counter2 += 2;
//        Object o = U.getObjectVolatile(arr2, ((long) 3 << ASHIFT) + ABASE);
        long l = counter2;
    }

    public static void main(String[] args) throws Exception {
        VolatileBenchmark vb = new VolatileBenchmark();
        vb.normal();
        vb.volat();

        Runner.run(VolatileBenchmark.class);
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe U;
    private static final long ABASE;
    private static final int ASHIFT;

    static {
        try {
            U = createUnsafe();
            Class<?> ak = Object[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static Unsafe createUnsafe() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
