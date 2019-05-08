package com.github.sisyphsu.benchmark.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sisyphsu.benchmark.pojo.Egg;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * JSON性能测试
 * <p>
 * Benchmark                            Mode  Cnt  Score   Error  Units
 * EggBenchmark.fastjson2TreeBenchmark  avgt    9  0.735 ± 0.033  us/op
 * EggBenchmark.fastjsonJsonBenchmark   avgt    9  0.593 ± 0.011  us/op
 * EggBenchmark.fastjsonJsonBenchmark2  avgt    9  1.185 ± 0.040  us/op
 * EggBenchmark.gson2TreeBenchmark      avgt    9  1.176 ± 0.311  us/op
 * EggBenchmark.gsonJsonBenchmark       avgt    9  1.555 ± 0.528  us/op
 * EggBenchmark.gsonJsonBenchmark2      avgt    9  1.733 ± 0.525  us/op
 * EggBenchmark.jackson2TreeBenchmark   avgt    9  0.562 ± 0.042  us/op
 * EggBenchmark.jacksonJsonBenchmark    avgt    9  0.524 ± 0.023  us/op
 * EggBenchmark.jacksonJsonBenchmark2   avgt    9  0.878 ± 0.061  us/op
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

}
