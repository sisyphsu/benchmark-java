package com.github.sisyphsu.benchmark.utils;

import com.github.sisyphsu.benchmark.Runner;
import org.openjdk.jmh.annotations.*;

import java.util.IdentityHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 数据集4096
 * Benchmark                      Mode  Cnt  Score   Error  Units
 * IdentityMapBenchmark.contains  avgt    9  1.937 ± 0.016  ns/op
 * IdentityMapBenchmark.get       avgt    9  2.061 ± 0.009  ns/op
 * IdentityMapBenchmark.put       avgt    9  2.988 ± 0.369  ns/op
 * <p>
 * 数据集256
 * Benchmark                      Mode  Cnt  Score   Error  Units
 * IdentityMapBenchmark.contains  avgt    9  1.946 ± 0.014  ns/op
 * IdentityMapBenchmark.get       avgt    9  2.070 ± 0.010  ns/op
 * IdentityMapBenchmark.put       avgt    9  2.957 ± 0.059  ns/op
 * <p>
 * 性能表现与数据量没有关系，比较典型的O(1)时间复杂度
 *
 * @author sulin
 * @since 2019-10-22 11:28:36
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class IdentityMapBenchmark {

    private static final IdentityHashMap<Object, Object> map  = new IdentityHashMap<>();
    private static final Object[]                        keys = new Object[1 << 8];

    static {
        for (int i = 0; i < keys.length; i++) {
            Object key = new Object();
            keys[i] = key;
            map.put(key, key);
        }
    }

    @Benchmark
    public void contains() {
        map.containsKey(keys[64]);
    }

    @Benchmark
    public void get() {
        map.get(keys[64]);
    }

    @Benchmark
    public void put() {
        map.put(keys[64], keys[64]);
    }

    public static void main(String[] args) {
        Runner.run(IdentityMapBenchmark.class);
    }

}
