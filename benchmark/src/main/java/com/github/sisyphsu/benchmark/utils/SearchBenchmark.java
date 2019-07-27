package com.github.sisyphsu.benchmark.utils;

import com.github.sisyphsu.benchmark.Runner;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 对比Map与BinarySearch的快速检索的性能
 * <p>
 * Item数量为64
 * Benchmark                                   Mode  Cnt    Score    Error   Units
 * SearchBenchmark.binary                      avgt    9  348.692 ±  3.918   ns/op
 * SearchBenchmark.map                         avgt    9  169.892 ±  2.776   ns/op
 * <p>
 * Item数量为256
 * Benchmark                                   Mode  Cnt     Score    Error   Units
 * SearchBenchmark.binary                      avgt    9  2014.112 ± 64.266   ns/op
 * SearchBenchmark.map                         avgt    9   890.706 ± 26.997   ns/op
 * <p>
 * Item数量为1024
 * Benchmark                                   Mode  Cnt      Score      Error   Units
 * SearchBenchmark.binary                      avgt    9  43502.328 ± 2760.750   ns/op
 * SearchBenchmark.map                         avgt    9   5046.955 ±  336.107   ns/op
 *
 * @author sulin
 * @since 2019-07-27 17:49:29
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SearchBenchmark {

    private static final int COUNT = 1024;
    private static final Item[] items = new Item[COUNT];
    private static final int[] itemHash = new int[COUNT];
    private static final short[] itemHashIndex = new short[COUNT];
    private static final Map<String, Item> itemMap = new ConcurrentHashMap<>();

    static {
        for (int i = 0; i < COUNT; i++) {
            Item item = new Item(RandomStringUtils.randomAlphanumeric(16), RandomUtils.nextDouble());
            items[i] = item;
            itemHash[i] = item.name.hashCode();
            itemMap.put(item.name, item);
        }
        Arrays.sort(itemHash);
        for (int i = 0; i < items.length; i++) {
            int offset = Arrays.binarySearch(itemHash, items[i].name.hashCode());
            itemHashIndex[offset] = (short) i;
        }
    }

    public static Item getByMap(String name) {
        return itemMap.get(name);
    }

    public static Item getByIndex(String name) {
        int hash = name.hashCode();
        int hashIndex = Arrays.binarySearch(itemHash, hash);
        if (hashIndex < 0 || hashIndex >= itemHashIndex.length)
            return null;
        int offset = itemHashIndex[hashIndex];
        if (offset < 0 || offset >= items.length)
            return null;
        return items[offset];
    }

    @Benchmark
    public void map() {
        for (Item item : items) {
            Double score = getByMap(item.name).score;
        }
    }

    @Benchmark
    public void binary() {
        for (Item item : items) {
            Double score = getByIndex(item.name).score;
        }
    }

    public static void main(String[] args) throws Exception {
        String name = items[0].name;
        Item item1 = getByMap(name);
        Item item2 = getByIndex(name);
        if (item1 != item2 || item1 == null || !item1.name.equals(name)) {
            throw new RuntimeException("error");
        }
        Runner.run(SearchBenchmark.class);
    }

    public static class Item {
        private String name;
        private Double score;

        public Item(String name, Double score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

}
