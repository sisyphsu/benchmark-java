package com.github.sisyphsu.benchmark.log;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark          Mode  Cnt     Score      Error  Units
 * LogBenchmark.test  avgt    9  6126.322 ± 1019.827  ns/op
 *
 * @author sulin
 * @since 2019-05-24 17:46:44
 */
@Warmup(iterations = 1, time = 1)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Slf4j
public class LogBenchmark {

    @Benchmark
    public void test() {
        BigDecimal baseLine = new BigDecimal(1);
        String postLan = "en";
        List<String> main = new ArrayList<>();
        List<String> scr = new ArrayList<>();
        List<String> def = new ArrayList<>();

        log.debug("Lan score, final weight:{}, post Lan:{}, mainLan:{}, secLan:{}, defLan:{}",
                baseLine.intValue(),
                postLan,
                main, scr, def);
    }

}
