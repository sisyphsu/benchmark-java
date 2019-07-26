package com.github.sisyphsu.benchmark.lang;

import com.github.sisyphsu.benchmark.Runner;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;

/**
 * Array反射性能测试
 * Benchmark(2048)         Mode  Cnt      Score     Error  Units
 * ArrayBenchmark.normal   avgt    9    705.398 ±   6.577  ns/op
 * ArrayBenchmark.reflect  avgt    9  92357.930 ± 839.332  ns/op
 *
 * @author sulin
 * @since 2019-07-26 10:38:31
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ArrayBenchmark {

    private static final int size = 2048;

    @Benchmark
    public void normal() {
        byte[] buf = new byte[size];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) i;
        }
    }

    @Benchmark
    public void reflect() {
        Object buf = Array.newInstance(byte.class, size);
        for (int i = 0; i < size; i++) {
            Array.set(buf, i, (byte) i);
        }
    }

    public static void main(String[] args) throws Exception {
        Runner.run(ArrayBenchmark.class);
    }

}
