package com.github.sisyphsu.benchmark.lang;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark            Mode  Cnt   Score   Error  Units
 * ArraysBenchmark.arr  avgt    9  50.953 ± 0.559  ns/op
 * ArraysBenchmark.dir  avgt    9  50.473 ± 0.225  ns/op
 *
 * @author sulin
 * @since 2019-09-07 12:35:05
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ArraysBenchmark {

    public static final int[] arr = new int[8];

    //    @Benchmark
    public void fill() {
        Arrays.fill(arr, -1);
    }

    @Benchmark
    public void arr() {
        for (int i = 0; i < 2; i++) {
            System.currentTimeMillis();
        }
    }

    @Benchmark
    public void dir() {
        System.currentTimeMillis();
        System.currentTimeMillis();
    }

}
