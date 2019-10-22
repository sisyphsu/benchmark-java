package com.github.sisyphsu.benchmark.utils;

import com.github.sisyphsu.benchmark.Runner;
import org.openjdk.jmh.annotations.*;

import java.util.IdentityHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark                      Mode  Cnt  Score   Error  Units
 * IdentityMapBenchmark.contains  avgt    9  1.937 ± 0.016  ns/op
 * IdentityMapBenchmark.get       avgt    9  2.061 ± 0.009  ns/op
 * IdentityMapBenchmark.put       avgt    9  2.988 ± 0.369  ns/op
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
    private static final Object[]                        keys = new Object[1 << 12];

    static {
        for (int i = 0; i < keys.length; i++) {
            Object key = new Object();
            keys[i] = key;
            map.put(key, key);
        }
    }

    @Benchmark
    public void contains() {
        map.containsKey(keys[1024]);
    }

    @Benchmark
    public void get() {
        map.get(keys[1024]);
    }

    @Benchmark
    public void put() {
        map.put(keys[1024], keys[1024]);
    }

    public static void main(String[] args) {
        Runner.run(IdentityMapBenchmark.class);
    }

}
