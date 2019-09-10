package com.github.sisyphsu.benchmark.lang;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark             Mode  Cnt   Score   Error  Units
 * StackBenchmark.heap   avgt    9  99.812 ± 0.496  ns/op
 * StackBenchmark.stack  avgt    9  82.316 ± 0.966  ns/op
 *
 * @author sulin
 * @since 2019-09-09 23:28:47
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class StackBenchmark {

    private static final int TARGET = 100;

    @Benchmark
    public void heap() {
        Pop pop = new Pop();
        this.revHeap(pop);
    }

    @Benchmark
    public void stack() {
        Pop pop = new Pop();
        this.revStack(pop, pop.times);
    }

    public void revHeap(Pop pop) {
        if (pop.times < TARGET) {
            pop.times++;
            revHeap(pop);
        }
    }

    public void revStack(Pop pop, int times) {
        if (times < TARGET) {
            revStack(pop, times + 1);
        } else {
            pop.times = times;
        }
    }

    public static class Pop {
        int times;
    }

}
