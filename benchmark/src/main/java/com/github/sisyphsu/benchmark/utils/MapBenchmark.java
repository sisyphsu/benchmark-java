package com.github.sisyphsu.benchmark.utils;

import com.github.sisyphsu.benchmark.Runner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 测试TreeMap与HashMap的性能对比
 * <p>
 * 第一轮测试 - 容量128
 * Benchmark                           Mode  Cnt  Score    Error  Units
 * TreeAndHashBenchmark.hashBenchmark  avgt    9  0.014 ±  0.001  us/op
 * TreeAndHashBenchmark.treeBenchmark  avgt    9  0.014 ±  0.002  us/op
 * <p>
 * 第二轮测试 - 容量1k
 * Benchmark                           Mode  Cnt  Score   Error  Units
 * TreeAndHashBenchmark.hashBenchmark  avgt    9  0.019 ± 0.002  us/op
 * TreeAndHashBenchmark.treeBenchmark  avgt    9  0.019 ± 0.003  us/op
 * <p>
 * 第三轮测试 - 容量64k
 * Benchmark                     Mode  Cnt  Score   Error  Units
 * MapBenchmark.hashBenchmark    avgt    9  0.022 ± 0.003  us/op
 * MapBenchmark.linkedBenchmark  avgt    9  0.024 ± 0.004  us/op
 * MapBenchmark.treeBenchmark    avgt    9  0.022 ± 0.003  us/op
 * <p>
 * <p>
 * <p>
 * 128只读get性能测试：
 * Benchmark                                             Mode  Cnt   Score    Error   Units
 * MapBenchmark.concurrentBenchmark                      avgt    9   2.150 ±  0.336   ns/op
 * MapBenchmark.hashBenchmark                            avgt    9   2.385 ±  0.824   ns/op
 * MapBenchmark.linkedBenchmark                          avgt    9   1.803 ±  0.024   ns/op
 * MapBenchmark.treeBenchmark                            avgt    9  16.419 ±  3.218   ns/op
 * 1K只读get性能测试：
 * Benchmark                                             Mode  Cnt   Score    Error   Units
 * MapBenchmark.concurrentBenchmark                      avgt    9   1.931 ±  0.176   ns/op
 * MapBenchmark.hashBenchmark                            avgt    9   2.052 ±  0.010   ns/op
 * MapBenchmark.linkedBenchmark                          avgt    9   2.139 ±  0.570   ns/op
 * MapBenchmark.treeBenchmark                            avgt    9  23.650 ±  3.106   ns/op
 * 10K只读get性能测试：
 * Benchmark                                             Mode  Cnt   Score    Error   Units
 * MapBenchmark.concurrentBenchmark                      avgt    9   2.028 ±  0.394   ns/op
 * MapBenchmark.hashBenchmark                            avgt    9   2.148 ±  0.224   ns/op
 * MapBenchmark.linkedBenchmark                          avgt    9   1.971 ±  0.221   ns/op
 * MapBenchmark.treeBenchmark                            avgt    9  43.367 ±  7.577   ns/op
 *
 * @author sulin
 * @since 2019-05-06 10:49:47
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Slf4j
public class MapBenchmark {

    private static final Map<String, Object> HASH_MAP       = new HashMap<>();
    private static final Map<String, Object> TREE_MAP       = new TreeMap<>();
    private static final Map<String, Object> LINKED_MAP     = new LinkedHashMap<>();
    private static final Map<String, Object> CONCURRENT_MAP = new ConcurrentHashMap<>();
    private static final String              KEY            = RandomStringUtils.randomAlphanumeric(16);
    private static final int                 INIT_SIZE      = 128;

    static {
        for (int i = 0; i < INIT_SIZE; i++) {
            String key = RandomStringUtils.randomAlphanumeric(16);
            HASH_MAP.put(key, key);
            TREE_MAP.put(key, key);
            LINKED_MAP.put(key, key);
            CONCURRENT_MAP.put(key, key);
        }
    }

    @Benchmark
    public void hashBenchmark() {
//        HASH_MAP.put(KEY, KEY);
        HASH_MAP.get(KEY);
//        HASH_MAP.remove(KEY);
    }

    @Benchmark
    public void linkedBenchmark() {
//        LINKED_MAP.put(KEY, KEY);
        LINKED_MAP.get(KEY);
//        LINKED_MAP.remove(KEY);
    }

    @Benchmark
    public void treeBenchmark() {
//        TREE_MAP.put(KEY, KEY);
        TREE_MAP.get(KEY);
//        TREE_MAP.remove(KEY);
    }

    @Benchmark
    public void concurrentBenchmark() {
//        CONCURRENT_MAP.put(KEY, KEY);
        CONCURRENT_MAP.get(KEY);
//        CONCURRENT_MAP.remove(KEY);
    }

    public static void main(String[] args) throws Exception {
        Runner.run(MapBenchmark.class);
    }

}
