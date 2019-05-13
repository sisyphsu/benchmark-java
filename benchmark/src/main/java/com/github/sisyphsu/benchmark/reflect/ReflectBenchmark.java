package com.github.sisyphsu.benchmark.reflect;

import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 反射的性能测试
 * <p>
 * 方法调用
 * Benchmark                      Mode  Cnt  Score    Error  Units
 * ReflectBenchmark.direct        avgt    9  0.026 ±  0.001  us/op
 * ReflectBenchmark.reflect       avgt    9  0.147 ±  0.004  us/op
 * ReflectBenchmark.reflectCache  avgt    9  0.028 ±  0.001  us/op
 *
 * @author sulin
 * @since 2019-05-13 20:49:14
 */
@Warmup(iterations = 1, time = 1)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ReflectBenchmark {

    private static final ReflectBenchmark B = new ReflectBenchmark();
    private static final Method method;

    static {
        try {
            method = ReflectBenchmark.class.getMethod("now");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void direct() {
        long ms = B.now();
    }

    @Benchmark
    public void reflect() throws Exception {
        Object ms = ReflectBenchmark.class.getMethod("now").invoke(B);
    }

    @Benchmark
    public void reflectCache() throws Exception {
        Object ms = method.invoke(B);
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws Exception {
        ReflectBenchmark b = new ReflectBenchmark();
        b.direct();
        b.reflect();
    }

}
