package com.github.sisyphsu.benchmark.lang;

import com.github.sisyphsu.benchmark.Runner;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * 测试Java8的函数式编程性能问题
 * Benchmark                 Mode  Cnt   Score   Error  Units
 * FunctionBenchmark.face    avgt    9  25.644 ± 0.123  ns/op
 * FunctionBenchmark.func    avgt    9  25.648 ± 0.116  ns/op
 * FunctionBenchmark.normal  avgt    9  25.396 ± 0.129  ns/op
 * 基本没有影响，和直接调用差不多一样，只是多了一次方法调用
 *
 * @author sulin
 * @since 2019-08-01 17:39:02
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FunctionBenchmark {

    private static Clock clock = FunctionBenchmark::nowMs;

    private static ClockFunc clockFunc = FunctionBenchmark::nowMs;

    public static long nowMs() {
        return System.currentTimeMillis();
    }

    @Benchmark
    public void normal() {
        long ms = nowMs();
    }

    @Benchmark
    public void face() {
        long ms = clock.now();
    }

    @Benchmark
    public void func() {
        long ms = clockFunc.now();
    }

    public interface Clock {
        long now();
    }

    @FunctionalInterface
    public interface ClockFunc {
        long now();
    }

    public static void main(String[] args) throws Exception {
        Runner.run(FunctionBenchmark.class);
    }

}
