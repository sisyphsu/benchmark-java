package com.github.sisyphsu.benchmark.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sisyphsu.benchmark.pojo.EggCake;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * 复杂对象EggCake的JSON性能测试
 * <p>
 * Benchmark                                Mode  Cnt  Score   Error  Units
 * EggCakeBenchmark.fastjson2TreeBenchmark  avgt    9  1.713 ± 0.046  us/op
 * EggCakeBenchmark.fastjsonJsonBenchmark   avgt    9  1.260 ± 0.033  us/op
 * EggCakeBenchmark.fastjsonJsonBenchmark2  avgt    9  2.961 ± 0.173  us/op
 * <p>
 * EggCakeBenchmark.gson2TreeBenchmark      avgt    9  2.394 ± 0.278  us/op
 * EggCakeBenchmark.gsonJsonBenchmark       avgt    9  3.305 ± 0.602  us/op
 * EggCakeBenchmark.gsonJsonBenchmark2      avgt    9  4.055 ± 0.655  us/op
 * <p>
 * EggCakeBenchmark.jackson2TreeBenchmark   avgt    9  1.209 ± 0.018  us/op
 * EggCakeBenchmark.jacksonJsonBenchmark    avgt    9  1.026 ± 0.015  us/op
 * EggCakeBenchmark.jacksonJsonBenchmark2   avgt    9  2.457 ± 0.056  us/op
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

}
