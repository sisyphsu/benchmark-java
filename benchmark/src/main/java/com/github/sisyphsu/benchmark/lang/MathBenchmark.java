package com.github.sisyphsu.benchmark.lang;

import com.github.sisyphsu.benchmark.Runner;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * &运算的效率是%的是十多倍：
 * Benchmark              Mode  Cnt  Score   Error  Units
 * MathBenchmark.testMod  avgt    9  0.504 ± 0.003  ns/op
 * MathBenchmark.testRem  avgt    9  7.738 ± 0.202  ns/op
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
public class MathBenchmark {

    private static long num = System.currentTimeMillis();
    private static long base = 256;
    private static long result;

    @Benchmark
    public void testMod() {
        result = num & base;
    }

    @Benchmark
    public void testRem() {
        result = num % base;
    }

    public static void main(String[] args) throws Exception {
        Runner.run(MathBenchmark.class);
    }

}
