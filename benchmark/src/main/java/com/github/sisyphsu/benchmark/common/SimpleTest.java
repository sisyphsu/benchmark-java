package com.github.sisyphsu.benchmark.common;

import com.github.sisyphsu.benchmark.Runner;
import org.openjdk.jmh.annotations.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author sulin
 * @since 2019-05-08 13:18:27
 */
@Warmup(iterations = 1, time = 1)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Measurement(iterations = 1, time = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SimpleTest {

    @Benchmark
    public void nothing() {
        new Date();
    }

    public static void main(String[] args) throws Exception {
        Runner.run(SimpleTest.class);
    }

}
