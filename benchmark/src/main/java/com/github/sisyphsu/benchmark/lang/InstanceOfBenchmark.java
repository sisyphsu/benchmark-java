package com.github.sisyphsu.benchmark.lang;

import com.github.sisyphsu.benchmark.Runner;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 测试instanceOf、isInstance、isAssignableFrom的性能：
 * <p>
 * Benchmark                                     Mode  Cnt   Score    Error   Units
 * InstanceOfBenchmark.testInstanceOf            avgt    9   1.838 ±  0.018   ns/op
 * InstanceOfBenchmark.testIsAssignableFrom      avgt    9   1.833 ±  0.015   ns/op
 * InstanceOfBenchmark.testIsInstance            avgt    9   1.825 ±  0.007   ns/op
 * <p>
 * 参考：https://stackoverflow.com/questions/496928/what-is-the-difference-between-instanceof-and-class-isassignablefrom
 *
 * @author sulin
 * @since 2019-07-24 10:35:20
 */
@Warmup(iterations = 2, time = 2)
@Fork(3)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Slf4j
public class InstanceOfBenchmark {

    public static final Object list = new ArrayList();

    @Benchmark
    public boolean testInstanceOf() {
        return list instanceof List;
    }

    @Benchmark
    public boolean testIsInstance() {
        return List.class.isInstance(list);
    }

    @Benchmark
    public boolean testIsAssignableFrom() {
        return List.class.isAssignableFrom(list.getClass());
    }

    public static void main(String[] args) throws Exception {
        Runner.run(InstanceOfBenchmark.class);
    }

}
