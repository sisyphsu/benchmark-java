package com.github.sisyphsu.benchmark.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sisyphsu.benchmark.Runner;
import com.github.sisyphsu.benchmark.pojo.Egg;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * JSON性能测试
 * <p>
 * Benchmark                                                             Mode  Cnt     Score     Error   Units
 * EggBenchmark.fastjson2TreeBenchmark                                   avgt    9     0.747 ±   0.026   us/op
 * EggBenchmark.fastjson2TreeBenchmark:·gc.alloc.rate                    avgt    9   927.149 ±  32.170  MB/sec
 * EggBenchmark.fastjson2TreeBenchmark:·gc.alloc.rate.norm               avgt    9   848.017 ±   0.056    B/op
 * EggBenchmark.fastjson2TreeBenchmark:·gc.churn.PS_Eden_Space           avgt    9   930.968 ± 101.091  MB/sec
 * EggBenchmark.fastjson2TreeBenchmark:·gc.churn.PS_Eden_Space.norm      avgt    9   850.902 ±  66.790    B/op
 * EggBenchmark.fastjson2TreeBenchmark:·gc.churn.PS_Survivor_Space       avgt    9     0.084 ±   0.034  MB/sec
 * EggBenchmark.fastjson2TreeBenchmark:·gc.churn.PS_Survivor_Space.norm  avgt    9     0.077 ±   0.031    B/op
 * EggBenchmark.fastjson2TreeBenchmark:·gc.count                         avgt    9   107.000            counts
 * EggBenchmark.fastjson2TreeBenchmark:·gc.time                          avgt    9    74.000                ms
 * EggBenchmark.fastjson2TreeBenchmark:·mem.heap                         avgt    9   318.875                MB
 * EggBenchmark.fastjson2TreeBenchmark:·mem.nonheap                      avgt    9    16.920                MB
 * EggBenchmark.fastjsonJsonBenchmark                                    avgt    9     0.608 ±   0.014   us/op
 * EggBenchmark.fastjsonJsonBenchmark:·gc.alloc.rate                     avgt    9  1203.832 ±  26.795  MB/sec
 * EggBenchmark.fastjsonJsonBenchmark:·gc.alloc.rate.norm                avgt    9   896.019 ±   0.050    B/op
 * EggBenchmark.fastjsonJsonBenchmark:·gc.churn.PS_Eden_Space            avgt    9  1214.350 ±  77.135  MB/sec
 * EggBenchmark.fastjsonJsonBenchmark:·gc.churn.PS_Eden_Space.norm       avgt    9   903.729 ±  47.028    B/op
 * EggBenchmark.fastjsonJsonBenchmark:·gc.churn.PS_Survivor_Space        avgt    9     0.137 ±   0.058  MB/sec
 * EggBenchmark.fastjsonJsonBenchmark:·gc.churn.PS_Survivor_Space.norm   avgt    9     0.102 ±   0.042    B/op
 * EggBenchmark.fastjsonJsonBenchmark:·gc.count                          avgt    9   186.000            counts
 * EggBenchmark.fastjsonJsonBenchmark:·gc.time                           avgt    9   123.000                ms
 * EggBenchmark.fastjsonJsonBenchmark:·mem.heap                          avgt    9   336.087                MB
 * EggBenchmark.fastjsonJsonBenchmark:·mem.nonheap                       avgt    9    17.128                MB
 * EggBenchmark.fastjsonJsonBenchmark2                                   avgt    9     1.221 ±   0.038   us/op
 * EggBenchmark.fastjsonJsonBenchmark2:·gc.alloc.rate                    avgt    9  1038.519 ±  31.789  MB/sec
 * EggBenchmark.fastjsonJsonBenchmark2:·gc.alloc.rate.norm               avgt    9  1552.026 ±   0.065    B/op
 * EggBenchmark.fastjsonJsonBenchmark2:·gc.churn.PS_Eden_Space           avgt    9  1049.052 ±  58.764  MB/sec
 * EggBenchmark.fastjsonJsonBenchmark2:·gc.churn.PS_Eden_Space.norm      avgt    9  1567.416 ±  44.476    B/op
 * EggBenchmark.fastjsonJsonBenchmark2:·gc.churn.PS_Survivor_Space       avgt    9     0.115 ±   0.030  MB/sec
 * EggBenchmark.fastjsonJsonBenchmark2:·gc.churn.PS_Survivor_Space.norm  avgt    9     0.172 ±   0.045    B/op
 * EggBenchmark.fastjsonJsonBenchmark2:·gc.count                         avgt    9   128.000            counts
 * EggBenchmark.fastjsonJsonBenchmark2:·gc.time                          avgt    9    86.000                ms
 * EggBenchmark.fastjsonJsonBenchmark2:·mem.heap                         avgt    9   264.760                MB
 * EggBenchmark.fastjsonJsonBenchmark2:·mem.nonheap                      avgt    9    17.354                MB
 * <p>
 * EggBenchmark.gson2TreeBenchmark                                       avgt    9     1.173 ±   0.234   us/op
 * EggBenchmark.gson2TreeBenchmark:·gc.alloc.rate                        avgt    9  1363.102 ± 251.653  MB/sec
 * EggBenchmark.gson2TreeBenchmark:·gc.alloc.rate.norm                   avgt    9  1936.025 ±   0.063    B/op
 * EggBenchmark.gson2TreeBenchmark:·gc.churn.PS_Eden_Space               avgt    9  1378.889 ± 219.550  MB/sec
 * EggBenchmark.gson2TreeBenchmark:·gc.churn.PS_Eden_Space.norm          avgt    9  1962.014 ±  88.064    B/op
 * EggBenchmark.gson2TreeBenchmark:·gc.churn.PS_Survivor_Space           avgt    9     0.156 ±   0.044  MB/sec
 * EggBenchmark.gson2TreeBenchmark:·gc.churn.PS_Survivor_Space.norm      avgt    9     0.222 ±   0.049    B/op
 * EggBenchmark.gson2TreeBenchmark:·gc.count                             avgt    9   221.000            counts
 * EggBenchmark.gson2TreeBenchmark:·gc.time                              avgt    9   146.000                ms
 * EggBenchmark.gson2TreeBenchmark:·mem.heap                             avgt    9   163.809                MB
 * EggBenchmark.gson2TreeBenchmark:·mem.nonheap                          avgt    9    16.444                MB
 * EggBenchmark.gsonJsonBenchmark                                        avgt    9     1.534 ±   0.393   us/op
 * EggBenchmark.gsonJsonBenchmark:·gc.alloc.rate                         avgt    9  1280.465 ± 297.423  MB/sec
 * EggBenchmark.gsonJsonBenchmark:·gc.alloc.rate.norm                    avgt    9  2360.307 ±   0.663    B/op
 * EggBenchmark.gsonJsonBenchmark:·gc.churn.PS_Eden_Space                avgt    9  1284.088 ± 314.361  MB/sec
 * EggBenchmark.gsonJsonBenchmark:·gc.churn.PS_Eden_Space.norm           avgt    9  2365.121 ± 107.889    B/op
 * EggBenchmark.gsonJsonBenchmark:·gc.churn.PS_Survivor_Space            avgt    9     0.131 ±   0.052  MB/sec
 * EggBenchmark.gsonJsonBenchmark:·gc.churn.PS_Survivor_Space.norm       avgt    9     0.242 ±   0.082    B/op
 * EggBenchmark.gsonJsonBenchmark:·gc.count                              avgt    9   201.000            counts
 * EggBenchmark.gsonJsonBenchmark:·gc.time                               avgt    9   137.000                ms
 * EggBenchmark.gsonJsonBenchmark:·mem.heap                              avgt    9   276.145                MB
 * EggBenchmark.gsonJsonBenchmark:·mem.nonheap                           avgt    9    16.640                MB
 * EggBenchmark.gsonJsonBenchmark2                                       avgt    9     1.724 ±   0.378   us/op
 * EggBenchmark.gsonJsonBenchmark2:·gc.alloc.rate                        avgt    9  1475.437 ± 297.566  MB/sec
 * EggBenchmark.gsonJsonBenchmark2:·gc.alloc.rate.norm                   avgt    9  3072.019 ±   0.063    B/op
 * EggBenchmark.gsonJsonBenchmark2:·gc.churn.PS_Eden_Space               avgt    9  1481.987 ± 315.608  MB/sec
 * EggBenchmark.gsonJsonBenchmark2:·gc.churn.PS_Eden_Space.norm          avgt    9  3084.232 ± 150.068    B/op
 * EggBenchmark.gsonJsonBenchmark2:·gc.churn.PS_Survivor_Space           avgt    9     0.252 ±   0.402  MB/sec
 * EggBenchmark.gsonJsonBenchmark2:·gc.churn.PS_Survivor_Space.norm      avgt    9     0.550 ±   1.012    B/op
 * EggBenchmark.gsonJsonBenchmark2:·gc.count                             avgt    9   264.000            counts
 * EggBenchmark.gsonJsonBenchmark2:·gc.time                              avgt    9   178.000                ms
 * EggBenchmark.gsonJsonBenchmark2:·mem.heap                             avgt    9   273.867                MB
 * EggBenchmark.gsonJsonBenchmark2:·mem.nonheap                          avgt    9    16.862                MB
 * <p>
 * EggBenchmark.jackson2TreeBenchmark                                    avgt    9     0.563 ±   0.036   us/op
 * EggBenchmark.jackson2TreeBenchmark:·gc.alloc.rate                     avgt    9  1718.155 ± 107.783  MB/sec
 * EggBenchmark.jackson2TreeBenchmark:·gc.alloc.rate.norm                avgt    9  1184.016 ±   0.042    B/op
 * EggBenchmark.jackson2TreeBenchmark:·gc.churn.PS_Eden_Space            avgt    9  1750.592 ±  81.823  MB/sec
 * EggBenchmark.jackson2TreeBenchmark:·gc.churn.PS_Eden_Space.norm       avgt    9  1207.359 ±  69.639    B/op
 * EggBenchmark.jackson2TreeBenchmark:·gc.churn.PS_Survivor_Space        avgt    9     0.105 ±   0.051  MB/sec
 * EggBenchmark.jackson2TreeBenchmark:·gc.churn.PS_Survivor_Space.norm   avgt    9     0.072 ±   0.033    B/op
 * EggBenchmark.jackson2TreeBenchmark:·gc.count                          avgt    9   165.000            counts
 * EggBenchmark.jackson2TreeBenchmark:·gc.time                           avgt    9   123.000                ms
 * EggBenchmark.jackson2TreeBenchmark:·mem.heap                          avgt    9   310.955                MB
 * EggBenchmark.jackson2TreeBenchmark:·mem.nonheap                       avgt    9    16.817                MB
 * EggBenchmark.jacksonJsonBenchmark                                     avgt    9     0.533 ±   0.027   us/op
 * EggBenchmark.jacksonJsonBenchmark:·gc.alloc.rate                      avgt    9  1213.183 ±  60.538  MB/sec
 * EggBenchmark.jacksonJsonBenchmark:·gc.alloc.rate.norm                 avgt    9   792.015 ±   0.039    B/op
 * EggBenchmark.jacksonJsonBenchmark:·gc.churn.PS_Eden_Space             avgt    9  1222.972 ±  82.568  MB/sec
 * EggBenchmark.jacksonJsonBenchmark:·gc.churn.PS_Eden_Space.norm        avgt    9   798.399 ±  35.744    B/op
 * EggBenchmark.jacksonJsonBenchmark:·gc.churn.PS_Survivor_Space         avgt    9     0.135 ±   0.055  MB/sec
 * EggBenchmark.jacksonJsonBenchmark:·gc.churn.PS_Survivor_Space.norm    avgt    9     0.088 ±   0.033    B/op
 * EggBenchmark.jacksonJsonBenchmark:·gc.count                           avgt    9   190.000            counts
 * EggBenchmark.jacksonJsonBenchmark:·gc.time                            avgt    9   126.000                ms
 * EggBenchmark.jacksonJsonBenchmark:·mem.heap                           avgt    9   321.424                MB
 * EggBenchmark.jacksonJsonBenchmark:·mem.nonheap                        avgt    9    16.649                MB
 * EggBenchmark.jacksonJsonBenchmark2                                    avgt    9     0.918 ±   0.126   us/op
 * EggBenchmark.jacksonJsonBenchmark2:·gc.alloc.rate                     avgt    9  1694.654 ± 203.978  MB/sec
 * EggBenchmark.jacksonJsonBenchmark2:·gc.alloc.rate.norm                avgt    9  1896.016 ±   0.058    B/op
 * EggBenchmark.jacksonJsonBenchmark2:·gc.churn.PS_Eden_Space            avgt    9  1720.649 ± 216.539  MB/sec
 * EggBenchmark.jacksonJsonBenchmark2:·gc.churn.PS_Eden_Space.norm       avgt    9  1925.052 ±  78.701    B/op
 * EggBenchmark.jacksonJsonBenchmark2:·gc.churn.PS_Survivor_Space        avgt    9     0.110 ±   0.037  MB/sec
 * EggBenchmark.jacksonJsonBenchmark2:·gc.churn.PS_Survivor_Space.norm   avgt    9     0.123 ±   0.034    B/op
 * EggBenchmark.jacksonJsonBenchmark2:·gc.count                          avgt    9   153.000            counts
 * EggBenchmark.jacksonJsonBenchmark2:·gc.time                           avgt    9   111.000                ms
 * EggBenchmark.jacksonJsonBenchmark2:·mem.heap                          avgt    9   415.689                MB
 * EggBenchmark.jacksonJsonBenchmark2:·mem.nonheap                       avgt    9    16.937                MB
 *
 * @author sulin
 * @since 2019-05-08 10:40:22
 */
@Warmup(iterations = 1, time = 1)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class EggBenchmark {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Gson gson = new Gson();

    @Benchmark
    public void jackson2TreeBenchmark() {
        Egg egg = new Egg();
        mapper.valueToTree(egg);
    }

    @Benchmark
    public void jacksonJsonBenchmark() throws JsonProcessingException {
        Egg egg = new Egg();
        mapper.writeValueAsString(egg);
    }

    @Benchmark
    public void jacksonJsonBenchmark2() {
        Egg egg = new Egg();
        mapper.valueToTree(egg).toString();
    }

    @Benchmark
    public void gson2TreeBenchmark() {
        Egg egg = new Egg();
        gson.toJsonTree(egg);
    }

    @Benchmark
    public void gsonJsonBenchmark() {
        Egg egg = new Egg();
        gson.toJson(egg);
    }

    @Benchmark
    public void gsonJsonBenchmark2() {
        Egg egg = new Egg();
        gson.toJsonTree(egg).toString();
    }

    @Benchmark
    public void fastjson2TreeBenchmark() {
        Egg egg = new Egg();
        JSON.toJSON(egg);
    }

    @Benchmark
    public void fastjsonJsonBenchmark() {
        Egg egg = new Egg();
        JSON.toJSONString(egg);
    }

    @Benchmark
    public void fastjsonJsonBenchmark2() {
        Egg egg = new Egg();
        ((JSONObject) JSON.toJSON(egg)).toJSONString();
    }

    public static void main(String[] args) throws Exception {
        Runner.run(EggBenchmark.class);
    }

}
