package com.github.sisyphsu.benchmark.reflect;

import com.github.sisyphsu.benchmark.Runner;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
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
 * <p>
 * 增加Cglib、改用ns
 * Benchmark                         Mode  Cnt    Score   Error  Units
 * ReflectBenchmark.direct           avgt    9   25.497 ± 0.170  ns/op
 * ReflectBenchmark.reflect          avgt    9  149.775 ± 1.556  ns/op
 * ReflectBenchmark.reflectAccessor  avgt    9   26.033 ± 0.561  ns/op
 * ReflectBenchmark.reflectCglib     avgt    9   28.733 ± 1.328  ns/op
 * ReflectBenchmark.reflectMethod    avgt    9   28.446 ± 0.715  ns/op
 * <p>
 * Benchmark                                      Mode  Cnt     Score    Error   Units
 * ReflectBenchmark.direct                        avgt    9    25.678 ±  0.341   ns/op
 * ReflectBenchmark.reflectAccessor               avgt    9    25.938 ±  0.193   ns/op
 * ReflectBenchmark.reflectCglib                  avgt    9    29.128 ±  0.251   ns/op
 * ReflectBenchmark.reflectMethod                 avgt    9    28.295 ±  0.325   ns/op
 * ReflectBenchmark.reflectMethodOverrided        avgt    9    27.651 ±  0.269   ns/op
 *
 * @author sulin
 * @since 2019-05-13 20:49:14
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ReflectBenchmark {

    private static final ReflectBenchmark B = new ReflectBenchmark();
    private static final Method method;
    private static final Method methodOverrided;
    private static final MethodAccessor accessor;

    private static final FastClass cglibClass;
    private static final FastMethod cglibMethod;

    static {
        try {
            method = ReflectBenchmark.class.getMethod("now");
            methodOverrided = ReflectBenchmark.class.getMethod("now");
            methodOverrided.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        accessor = ReflectionFactory.getReflectionFactory().newMethodAccessor(method);
        cglibClass = FastClass.create(B.getClass());
        cglibMethod = cglibClass.getMethod(method);
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
    public void reflectMethodOverrided() throws Exception {
        Object ms = methodOverrided.invoke(B);
    }

    @Benchmark
    public void reflectAccessor() throws Exception {
        Object ms = accessor.invoke(B, null);
    }

    @Benchmark
    public void reflectCglib() throws Exception {
        Object ms = cglibMethod.invoke(B, null);
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws Exception {
        ReflectBenchmark b = new ReflectBenchmark();
        b.direct();
        b.reflect();
        b.reflectAccessor();
        b.reflectCglib();

        Runner.run(ReflectBenchmark.class);
    }

}
