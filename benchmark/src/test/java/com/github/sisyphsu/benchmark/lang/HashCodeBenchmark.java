package com.github.sisyphsu.benchmark.lang;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 测试一下字符串数组的hashcode效率
 * Benchmark                   Mode  Cnt  Score   Error  Units
 * HashCodeBenchmark.array     avgt    6  1.036 ± 0.005  ns/op
 * HashCodeBenchmark.hashcode  avgt    6  1.038 ± 0.006  ns/op
 * HashCodeBenchmark.normal    avgt    6  1.033 ± 0.002  ns/op
 * HashCodeBenchmark.string    avgt    6  0.776 ± 0.005  ns/op
 *
 * @author sulin
 * @since 2019-09-27 12:06:58
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class HashCodeBenchmark {

    private static final String   STR = RandomStringUtils.random(16);
    private static final String[] ARR = new String[16];

    static {
        for (int i = 0; i < ARR.length; i++) {
            ARR[i] = RandomStringUtils.random(16);
        }
    }

    @Benchmark
    public void string() {
        Objects.hashCode(STR);
    }

    @Benchmark
    public void array() {
        Objects.hashCode(ARR);
    }

    @Benchmark
    public void normal() {
        System.identityHashCode(ARR);
    }

    @Benchmark
    public void hashcode() {
        String.class.hashCode();
    }

}
