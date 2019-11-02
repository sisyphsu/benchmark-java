package com.github.sisyphsu.benchmark.lang;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author sulin
 * @since 2019-10-30 17:15:36
 */
@Warmup(iterations = 2, time = 2)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class StackBenchmark {

    private static final byte[]          buf = new byte[1024];
    private static       ByteArrayWriter baw = new ByteArrayWriter(new byte[1024]);

    @Benchmark
    public void direct() {
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) i;
        }
    }

    @Benchmark
    public void writer() {
        Writer writer = baw;
        baw.off = 0;
        for (int i = 0; i < buf.length; i++) {
            writer.write((byte) i);
        }
    }

    public interface Writer {
        void write(byte b);
    }

    public static class ByteArrayWriter implements Writer {
        int    off;
        byte[] buf;

        public ByteArrayWriter(byte[] buf) {
            this.buf = buf;
        }

        @Override
        public void write(byte b) {
            buf[off++] = b;
        }
    }

}
