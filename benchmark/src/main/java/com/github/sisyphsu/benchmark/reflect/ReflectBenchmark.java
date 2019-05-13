package com.github.sisyphsu.benchmark.reflect;

import org.openjdk.jmh.annotations.*;
import sun.reflect.MethodAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 反射的性能测试
 * <p>
 * 方法调用
 * Benchmark                         Mode  Cnt  Score    Error  Units
 * ReflectBenchmark.direct           avgt    9  0.026 ±  0.001  us/op
 * ReflectBenchmark.reflect          avgt    9  0.147 ±  0.002  us/op
 * ReflectBenchmark.reflectMethod    avgt    9  0.028 ±  0.001  us/op
 * ReflectBenchmark.reflectAccessor  avgt    9  0.026 ±  0.001  us/op
 * s
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
    private static final MethodAccessor accessor;

    static {
        try {
            method = ReflectBenchmark.class.getMethod("now");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        accessor = ReflectionFactory.getReflectionFactory().newMethodAccessor(method);
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
    public void reflectMethod() throws Exception {
        Object ms = method.invoke(B);
    }

    @Benchmark
    public void reflectAccessor() throws Exception {
        Object ms = accessor.invoke(B, null);
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws Exception {
        ReflectBenchmark b = new ReflectBenchmark();
        b.direct();
        b.reflect();
        b.reflectAccessor();
    }

}
