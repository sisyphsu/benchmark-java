package com.github.sisyphsu.benchmark.lang;

import com.github.sisyphsu.benchmark.Runner;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * 方法调用性能测试
 * Benchmark                                  Mode  Cnt   Score    Error   Units
 * FuncBenchmark.callNow                      avgt    9  25.641 ±  0.297   ns/op
 * FuncBenchmark.direct                       avgt    9  25.692 ±  0.153   ns/op
 * <p>
 * 看来方法调用经过JVM优化基本没有负担了
 *
 * @author sulin
 * @since 2019-07-29 18:47:20
 */
@Slf4j
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FuncBenchmark {

    public long now() {
        return System.currentTimeMillis();
    }

    @Benchmark
    public void direct() {
        long time = System.currentTimeMillis();
    }

    @Benchmark
    public void callNow() {
        long time = now();
    }

    public static void main(String[] args) throws Exception {
        Runner.run(FuncBenchmark.class);
    }

}
