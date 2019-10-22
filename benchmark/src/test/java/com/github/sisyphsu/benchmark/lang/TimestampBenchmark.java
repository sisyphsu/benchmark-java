package com.github.sisyphsu.benchmark.lang;

import org.openjdk.jmh.annotations.*;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark                     Mode  Cnt   Score   Error  Units
 * TimestampBenchmark.now        avgt    9  25.500 ± 0.098  ns/op
 * TimestampBenchmark.now2       avgt    9  25.538 ± 0.108  ns/op
 * TimestampBenchmark.startTime  avgt    9   0.257 ± 0.003  ns/op
 * TimestampBenchmark.uptime     avgt    9  74.121 ± 0.704  ns/op
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

    private static final RuntimeMXBean MX_BEAN = ManagementFactory.getRuntimeMXBean();

    @Benchmark
    public void now() {
        System.currentTimeMillis();
    }

    @Benchmark
    public void now2() {
        this.now();
    }

    @Benchmark
    public void uptime() {
        MX_BEAN.getUptime();
    }

    @Benchmark
    public void startTime() {
        MX_BEAN.getStartTime();
    }

}
