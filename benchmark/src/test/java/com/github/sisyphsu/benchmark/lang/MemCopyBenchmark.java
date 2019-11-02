package com.github.sisyphsu.benchmark.lang;

import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * 每一轮都分配内存：
 * Benchmark                Mode  Cnt    Score   Error  Units
 * MemCopyBenchmark.forcpy  avgt    9  139.784 ± 1.666  ns/op
 * MemCopyBenchmark.syscpy  avgt    9   90.644 ± 0.839  ns/op
 * 复用内存的话：
 * Benchmark                Mode  Cnt   Score   Error  Units
 * MemCopyBenchmark.forcpy  avgt    9  50.387 ± 2.127  ns/op
 * MemCopyBenchmark.syscpy  avgt    9  13.109 ± 0.316  ns/op
 * <p>
 * 申请1k的内存耗时大概80ns，内存复制的性能上，syscpy还是挺快的。
 *
 * @author sulin
 * @since 2019-10-28 14:53:15
 */
@Warmup(iterations = 2, time = 2)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MemCopyBenchmark {

    private static final byte[] data = RandomUtils.nextBytes(1024);
    private static final byte[] tmp  = new byte[data.length];

    @Benchmark
    public void syscpy() {
//        byte[] tmp = new byte[data.length];
        System.arraycopy(data, 0, tmp, 0, data.length);
    }

    @Benchmark
    public void forcpy() {
//        byte[] tmp = new byte[data.length];
        for (int i = 0, len = data.length; i < len; i++) {
            tmp[i] = data[i];
        }
    }

}
