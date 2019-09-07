package com.github.sisyphsu.benchmark.lang;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark               Mode  Cnt   Score   Error  Units
 * TimestampBenchmark.now  avgt    9  25.697 ± 0.139  ns/op
 *
 * @author sulin
 * @since 2019-06-16 13:27:11
 */
@Warmup(iterations = 1, time = 1)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class TimestampBenchmark {

    @Benchmark
    public void now() {
        System.currentTimeMillis();
    }

    @Benchmark
    public void now2() {
        this.now();
    }

}
