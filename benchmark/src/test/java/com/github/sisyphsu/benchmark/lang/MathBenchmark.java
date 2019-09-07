package com.github.sisyphsu.benchmark.lang;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark                Mode  Cnt   Score   Error  Units
 * MathBenchmark.benchmark  avgt    9  25.598 ± 0.198  ns/op
 * MathBenchmark.timestamp  avgt    9  25.525 ± 0.243  ns/op
 *
 * @author sulin
 * @since 2019-09-07 12:30:06
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MathBenchmark {

    @Benchmark
    public void timestamp() {
        System.currentTimeMillis();
    }

    @Benchmark
    public void benchmark() {
        long x = System.currentTimeMillis() * 2 + 1;
    }

}
