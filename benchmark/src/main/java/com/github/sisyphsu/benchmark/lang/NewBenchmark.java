package com.github.sisyphsu.benchmark.lang;

import lombok.Data;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark               Mode  Cnt  Score   Error  Units
 * NewBenchmark.benchmark  avgt    9  0.259 ± 0.002  ns/op
 *
 * @author sulin
 * @since 2019-10-22 11:40:04
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class NewBenchmark {

    @Benchmark
    public void benchmark() {
        new Person();
    }

    @Data
    public static class Person {
        private int    id;
        private long   timestamp;
        private String name;
    }

}
