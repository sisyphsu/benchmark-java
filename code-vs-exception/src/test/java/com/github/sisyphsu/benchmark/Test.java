package com.github.sisyphsu.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * 性能测试
 * <p>
 * Test.test                  avgt    5    26.791 ±   0.400  ns/op
 * Test.testException         avgt    5  1059.158 ±  75.187  ns/op
 * Test.testDeep10Exception   avgt    5  1788.066 ±  47.961  ns/op
 * Test.testDeep100Exception  avgt    5  9787.283 ± 418.806  ns/op
 * <p>
 * Test.test                  avgt    5  0.027 ± 0.001  us/op
 * Test.testDeep100Exception  avgt    5  9.802 ± 0.411  us/op
 * Test.testDeep10Exception   avgt    5  1.826 ± 0.122  us/op
 * Test.testException         avgt    5  1.060 ± 0.045  us/op
 *
 * @author sulin
 * @since 2019-03-10 13:33:14
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 3)
@State(Scope.Thread)
public class Test {

    private BizComponent biz = new BizComponent();

    @Benchmark
    public void test() {
        BizResult result = biz.mockResult();
        int code = result.getCode();
        String msg = result.getMsg();
    }

    @Benchmark
    public void testException() {
        try {
            biz.mockException(1);
        } catch (BizException e) {
            int code = e.getCode();
            String msg = e.getMsg();
        }
    }

    @Benchmark
    public void testDeep10Exception() {
        try {
            biz.mockException(10);
        } catch (BizException e) {
            int code = e.getCode();
            String msg = e.getMsg();
        }
    }

    @Benchmark
    public void testDeep100Exception() {
        try {
            biz.mockException(100);
        } catch (BizException e) {
            int code = e.getCode();
            String msg = e.getMsg();
        }
    }

}
