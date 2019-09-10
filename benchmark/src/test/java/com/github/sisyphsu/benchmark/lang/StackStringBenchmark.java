package com.github.sisyphsu.benchmark.lang;

import org.junit.Test;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * String与offset都在stack参数中：
 * Benchmark                   Mode  Cnt   Score   Error  Units
 * StackStringBenchmark.heap   avgt    9  76.571 ± 0.334  ns/op
 * StackStringBenchmark.stack  avgt    9  65.613 ± 0.400  ns/op
 * <p>
 * 将String从参数中踢出：
 * Benchmark                   Mode  Cnt   Score   Error  Units
 * StackStringBenchmark.heap   avgt    9  75.431 ± 0.448  ns/op
 * StackStringBenchmark.stack  avgt    9  68.243 ± 0.360  ns/op
 *
 * @author sulin
 * @since 2019-09-09 23:28:47
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class StackStringBenchmark {

    private static final String STR = "hello world!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";

    @Benchmark
    public void heap() {
        Result pop = new Result(STR);
        this.revHeap(pop);
    }

    @Benchmark
    public void stack() {
        Result pop = new Result(STR);
        this.revStack(pop, 0, 0);
    }

    public void revHeap(Result pop) {
        if (pop.offset < pop.str.length()) {
            pop.sum += pop.str.charAt(pop.offset);
            pop.offset++;
            revHeap(pop);
        }
    }

    public void revStack(Result pop, int offset, int sum) {
        if (offset < pop.str.length()) {
            revStack(pop, offset + 1, sum + pop.str.charAt(offset));
        } else {
            pop.sum = sum;
        }
    }

    @Test
    public void test() {
        Result pop1 = new Result(STR);
        this.revHeap(pop1);

        Result pop2 = new Result(STR);
        this.revStack(pop2, 0, 0);

        assert pop1.sum == pop2.sum;
    }

    public static class Result {
        String str;
        int sum;
        int offset;

        public Result(String str) {
            this.str = str;
        }
    }

}
