package com.github.sisyphsu.benchmark.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sisyphsu.benchmark.Runner;
import com.github.sisyphsu.benchmark.pojo.EggCake;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * 复杂对象EggCake的JSON性能测试
 * <p>
 * Benchmark                                                                 Mode  Cnt     Score     Error   Units
 * EggCakeBenchmark.fastjson2TreeBenchmark                                   avgt    9     1.748 ±   0.056   us/op
 * EggCakeBenchmark.fastjson2TreeBenchmark:·gc.alloc.rate                    avgt    9  1035.537 ±  33.052  MB/sec
 * EggCakeBenchmark.fastjson2TreeBenchmark:·gc.alloc.rate.norm               avgt    9  2216.011 ±   0.052    B/op
 * EggCakeBenchmark.fastjson2TreeBenchmark:·gc.churn.PS_Eden_Space           avgt    9  1068.051 ±  66.146  MB/sec
 * EggCakeBenchmark.fastjson2TreeBenchmark:·gc.churn.PS_Eden_Space.norm      avgt    9  2285.362 ± 105.793    B/op
 * EggCakeBenchmark.fastjson2TreeBenchmark:·gc.churn.PS_Survivor_Space       avgt    9     0.120 ±   0.075  MB/sec
 * EggCakeBenchmark.fastjson2TreeBenchmark:·gc.churn.PS_Survivor_Space.norm  avgt    9     0.257 ±   0.164    B/op
 * EggCakeBenchmark.fastjson2TreeBenchmark:·gc.count                         avgt    9   169.000            counts
 * EggCakeBenchmark.fastjson2TreeBenchmark:·gc.time                          avgt    9   117.000                ms
 * EggCakeBenchmark.fastjson2TreeBenchmark:·mem.heap                         avgt    9   321.959                MB
 * EggCakeBenchmark.fastjson2TreeBenchmark:·mem.nonheap                      avgt    9    17.105                MB
 * EggCakeBenchmark.fastjsonJsonBenchmark                                    avgt    9     1.300 ±   0.053   us/op
 * EggCakeBenchmark.fastjsonJsonBenchmark:·gc.alloc.rate                     avgt    9  1036.073 ±  42.764  MB/sec
 * EggCakeBenchmark.fastjsonJsonBenchmark:·gc.alloc.rate.norm                avgt    9  1648.004 ±   0.053    B/op
 * EggCakeBenchmark.fastjsonJsonBenchmark:·gc.churn.PS_Eden_Space            avgt    9  1062.854 ±  81.960  MB/sec
 * EggCakeBenchmark.fastjsonJsonBenchmark:·gc.churn.PS_Eden_Space.norm       avgt    9  1690.964 ± 126.413    B/op
 * EggCakeBenchmark.fastjsonJsonBenchmark:·gc.churn.PS_Survivor_Space        avgt    9     0.119 ±   0.033  MB/sec
 * EggCakeBenchmark.fastjsonJsonBenchmark:·gc.churn.PS_Survivor_Space.norm   avgt    9     0.189 ±   0.048    B/op
 * EggCakeBenchmark.fastjsonJsonBenchmark:·gc.count                          avgt    9   161.000            counts
 * EggCakeBenchmark.fastjsonJsonBenchmark:·gc.time                           avgt    9   106.000                ms
 * EggCakeBenchmark.fastjsonJsonBenchmark:·mem.heap                          avgt    9   335.979                MB
 * EggCakeBenchmark.fastjsonJsonBenchmark:·mem.nonheap                       avgt    9    17.564                MB
 * EggCakeBenchmark.fastjsonJsonBenchmark2                                   avgt    9     2.983 ±   0.066   us/op
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·gc.alloc.rate                    avgt    9   943.987 ±  21.012  MB/sec
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·gc.alloc.rate.norm               avgt    9  3448.018 ±   0.082    B/op
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·gc.churn.PS_Eden_Space           avgt    9   966.029 ±  30.664  MB/sec
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·gc.churn.PS_Eden_Space.norm      avgt    9  3529.099 ± 138.415    B/op
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·gc.churn.PS_Survivor_Space       avgt    9     0.107 ±   0.033  MB/sec
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·gc.churn.PS_Survivor_Space.norm  avgt    9     0.390 ±   0.123    B/op
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·gc.count                         avgt    9   131.000            counts
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·gc.time                          avgt    9    91.000                ms
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·mem.heap                         avgt    9   336.419                MB
 * EggCakeBenchmark.fastjsonJsonBenchmark2:·mem.nonheap                      avgt    9    17.614                MB
 * EggCakeBenchmark.gson2TreeBenchmark                                       avgt    9     2.379 ±   0.136   us/op
 * EggCakeBenchmark.gson2TreeBenchmark:·gc.alloc.rate                        avgt    9  1445.854 ±  82.442  MB/sec
 * EggCakeBenchmark.gson2TreeBenchmark:·gc.alloc.rate.norm                   avgt    9  4208.025 ±   0.083    B/op
 * EggCakeBenchmark.gson2TreeBenchmark:·gc.churn.PS_Eden_Space               avgt    9  1448.841 ±  62.795  MB/sec
 * EggCakeBenchmark.gson2TreeBenchmark:·gc.churn.PS_Eden_Space.norm          avgt    9  4217.868 ±  79.006    B/op
 * EggCakeBenchmark.gson2TreeBenchmark:·gc.churn.PS_Survivor_Space           avgt    9     0.183 ±   0.064  MB/sec
 * EggCakeBenchmark.gson2TreeBenchmark:·gc.churn.PS_Survivor_Space.norm      avgt    9     0.533 ±   0.192    B/op
 * EggCakeBenchmark.gson2TreeBenchmark:·gc.count                             avgt    9   255.000            counts
 * EggCakeBenchmark.gson2TreeBenchmark:·gc.time                              avgt    9   169.000                ms
 * EggCakeBenchmark.gson2TreeBenchmark:·mem.heap                             avgt    9   140.894                MB
 * EggCakeBenchmark.gson2TreeBenchmark:·mem.nonheap                          avgt    9    16.608                MB
 * EggCakeBenchmark.gsonJsonBenchmark                                        avgt    9     3.261 ±   0.365   us/op
 * EggCakeBenchmark.gsonJsonBenchmark:·gc.alloc.rate                         avgt    9  1218.759 ± 129.427  MB/sec
 * EggCakeBenchmark.gsonJsonBenchmark:·gc.alloc.rate.norm                    avgt    9  4848.562 ±  40.343    B/op
 * EggCakeBenchmark.gsonJsonBenchmark:·gc.churn.PS_Eden_Space                avgt    9  1222.213 ± 125.868  MB/sec
 * EggCakeBenchmark.gsonJsonBenchmark:·gc.churn.PS_Eden_Space.norm           avgt    9  4863.367 ± 138.234    B/op
 * EggCakeBenchmark.gsonJsonBenchmark:·gc.churn.PS_Survivor_Space            avgt    9     0.190 ±   0.111  MB/sec
 * EggCakeBenchmark.gsonJsonBenchmark:·gc.churn.PS_Survivor_Space.norm       avgt    9     0.757 ±   0.445    B/op
 * EggCakeBenchmark.gsonJsonBenchmark:·gc.count                              avgt    9   301.000            counts
 * EggCakeBenchmark.gsonJsonBenchmark:·gc.time                               avgt    9   191.000                ms
 * EggCakeBenchmark.gsonJsonBenchmark:·mem.heap                              avgt    9    89.697                MB
 * EggCakeBenchmark.gsonJsonBenchmark:·mem.nonheap                           avgt    9    16.797                MB
 * EggCakeBenchmark.gsonJsonBenchmark2                                       avgt    9     3.905 ±   0.344   us/op
 * EggCakeBenchmark.gsonJsonBenchmark2:·gc.alloc.rate                        avgt    9  1356.227 ± 121.051  MB/sec
 * EggCakeBenchmark.gsonJsonBenchmark2:·gc.alloc.rate.norm                   avgt    9  6469.366 ± 107.471    B/op
 * EggCakeBenchmark.gsonJsonBenchmark2:·gc.churn.PS_Eden_Space               avgt    9  1367.061 ± 110.076  MB/sec
 * EggCakeBenchmark.gsonJsonBenchmark2:·gc.churn.PS_Eden_Space.norm          avgt    9  6523.458 ± 224.428    B/op
 * EggCakeBenchmark.gsonJsonBenchmark2:·gc.churn.PS_Survivor_Space           avgt    9     0.191 ±   0.096  MB/sec
 * EggCakeBenchmark.gsonJsonBenchmark2:·gc.churn.PS_Survivor_Space.norm      avgt    9     0.911 ±   0.448    B/op
 * EggCakeBenchmark.gsonJsonBenchmark2:·gc.count                             avgt    9   282.000            counts
 * EggCakeBenchmark.gsonJsonBenchmark2:·gc.time                              avgt    9   182.000                ms
 * EggCakeBenchmark.gsonJsonBenchmark2:·mem.heap                             avgt    9   217.601                MB
 * EggCakeBenchmark.gsonJsonBenchmark2:·mem.nonheap                          avgt    9    17.261                MB
 * EggCakeBenchmark.jackson2TreeBenchmark                                    avgt    9     1.237 ±   0.035   us/op
 * EggCakeBenchmark.jackson2TreeBenchmark:·gc.alloc.rate                     avgt    9  1822.458 ±  51.786  MB/sec
 * EggCakeBenchmark.jackson2TreeBenchmark:·gc.alloc.rate.norm                avgt    9  2760.019 ±   0.063    B/op
 * EggCakeBenchmark.jackson2TreeBenchmark:·gc.churn.PS_Eden_Space            avgt    9  1845.154 ±  57.515  MB/sec
 * EggCakeBenchmark.jackson2TreeBenchmark:·gc.churn.PS_Eden_Space.norm       avgt    9  2795.298 ± 131.920    B/op
 * EggCakeBenchmark.jackson2TreeBenchmark:·gc.churn.PS_Survivor_Space        avgt    9     0.143 ±   0.077  MB/sec
 * EggCakeBenchmark.jackson2TreeBenchmark:·gc.churn.PS_Survivor_Space.norm   avgt    9     0.216 ±   0.120    B/op
 * EggCakeBenchmark.jackson2TreeBenchmark:·gc.count                          avgt    9   209.000            counts
 * EggCakeBenchmark.jackson2TreeBenchmark:·gc.time                           avgt    9   149.000                ms
 * EggCakeBenchmark.jackson2TreeBenchmark:·mem.heap                          avgt    9   460.357                MB
 * EggCakeBenchmark.jackson2TreeBenchmark:·mem.nonheap                       avgt    9    17.377                MB
 * EggCakeBenchmark.jacksonJsonBenchmark                                     avgt    9     1.043 ±   0.015   us/op
 * EggCakeBenchmark.jacksonJsonBenchmark:·gc.alloc.rate                      avgt    9  1189.948 ±  17.564  MB/sec
 * EggCakeBenchmark.jacksonJsonBenchmark:·gc.alloc.rate.norm                 avgt    9  1520.017 ±   0.058    B/op
 * EggCakeBenchmark.jacksonJsonBenchmark:·gc.churn.PS_Eden_Space             avgt    9  1207.262 ±  65.333  MB/sec
 * EggCakeBenchmark.jacksonJsonBenchmark:·gc.churn.PS_Eden_Space.norm        avgt    9  1542.178 ±  82.513    B/op
 * EggCakeBenchmark.jacksonJsonBenchmark:·gc.churn.PS_Survivor_Space         avgt    9     0.150 ±   0.083  MB/sec
 * EggCakeBenchmark.jacksonJsonBenchmark:·gc.churn.PS_Survivor_Space.norm    avgt    9     0.192 ±   0.107    B/op
 * EggCakeBenchmark.jacksonJsonBenchmark:·gc.count                           avgt    9   214.000            counts
 * EggCakeBenchmark.jacksonJsonBenchmark:·gc.time                            avgt    9   136.000                ms
 * EggCakeBenchmark.jacksonJsonBenchmark:·mem.heap                           avgt    9   315.332                MB
 * EggCakeBenchmark.jacksonJsonBenchmark:·mem.nonheap                        avgt    9    16.963                MB
 * EggCakeBenchmark.jacksonJsonBenchmark2                                    avgt    9     2.436 ±   0.074   us/op
 * EggCakeBenchmark.jacksonJsonBenchmark2:·gc.alloc.rate                     avgt    9  2320.166 ±  56.205  MB/sec
 * EggCakeBenchmark.jacksonJsonBenchmark2:·gc.alloc.rate.norm                avgt    9  6920.012 ± 100.840    B/op
 * EggCakeBenchmark.jacksonJsonBenchmark2:·gc.churn.PS_Eden_Space            avgt    9  2355.906 ± 115.455  MB/sec
 * EggCakeBenchmark.jacksonJsonBenchmark2:·gc.churn.PS_Eden_Space.norm       avgt    9  7025.579 ± 231.008    B/op
 * EggCakeBenchmark.jacksonJsonBenchmark2:·gc.churn.PS_Survivor_Space        avgt    9     0.197 ±   0.092  MB/sec
 * EggCakeBenchmark.jacksonJsonBenchmark2:·gc.churn.PS_Survivor_Space.norm   avgt    9     0.589 ±   0.282    B/op
 * EggCakeBenchmark.jacksonJsonBenchmark2:·gc.count                          avgt    9   276.000            counts
 * EggCakeBenchmark.jacksonJsonBenchmark2:·gc.time                           avgt    9   195.000                ms
 * EggCakeBenchmark.jacksonJsonBenchmark2:·mem.heap                          avgt    9   314.497                MB
 * EggCakeBenchmark.jacksonJsonBenchmark2:·mem.nonheap                       avgt    9    17.435                MB
 *
 * @author sulin
 * @since 2019-05-08 11:24:51
 */
@Warmup(iterations = 1, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class EggCakeBenchmark {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Gson gson = new Gson();

    @Benchmark
    public void jackson2TreeBenchmark() {
        EggCake eggCake = new EggCake();
        mapper.valueToTree(eggCake);
    }

    @Benchmark
    public void jacksonJsonBenchmark() throws JsonProcessingException {
        EggCake eggCake = new EggCake();
        mapper.writeValueAsString(eggCake);
    }

    @Benchmark
    public void jacksonJsonBenchmark2() {
        EggCake eggCake = new EggCake();
        mapper.valueToTree(eggCake).toString();
    }

    @Benchmark
    public void gson2TreeBenchmark() {
        EggCake eggCake = new EggCake();
        gson.toJsonTree(eggCake);
    }

    @Benchmark
    public void gsonJsonBenchmark() {
        EggCake eggCake = new EggCake();
        gson.toJson(eggCake);
    }

    @Benchmark
    public void gsonJsonBenchmark2() {
        EggCake eggCake = new EggCake();
        gson.toJsonTree(eggCake).toString();
    }

    @Benchmark
    public void fastjson2TreeBenchmark() {
        EggCake eggCake = new EggCake();
        JSON.toJSON(eggCake);
    }

    @Benchmark
    public void fastjsonJsonBenchmark() {
        EggCake eggCake = new EggCake();
        JSON.toJSONString(eggCake);
    }

    @Benchmark
    public void fastjsonJsonBenchmark2() {
        EggCake eggCake = new EggCake();
        ((JSONObject) JSON.toJSON(eggCake)).toJSONString();
    }

    public static void main(String[] args) throws Exception {
        Runner.run(EggCakeBenchmark.class);
    }

}
