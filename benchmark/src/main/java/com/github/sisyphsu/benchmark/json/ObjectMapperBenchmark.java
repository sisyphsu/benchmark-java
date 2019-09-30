package com.github.sisyphsu.benchmark.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sisyphsu.benchmark.Runner;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author sulin
 * @since 2019-09-29 19:33:24
 */
@Warmup(iterations = 1, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ObjectMapperBenchmark {

    @Benchmark
    public void test() {
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static void main(String[] args) {
        Runner.run(ObjectMapperBenchmark.class);
    }

}
