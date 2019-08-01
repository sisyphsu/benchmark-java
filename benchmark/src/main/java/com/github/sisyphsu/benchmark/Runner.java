package com.github.sisyphsu.benchmark;

import com.github.sisyphsu.benchmark.common.MemoryProfiler;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author sulin
 * @since 2019-05-08 13:18:27
 */
public class Runner {

    private static boolean gc;
    private static boolean memory;

    public static void run(Class cls) {
        OptionsBuilder builder = new OptionsBuilder();
        builder.include(cls.getSimpleName());
        if (gc) {
            builder.addProfiler(GCProfiler.class);
        }
        if (memory) {
            builder.addProfiler(MemoryProfiler.class);
        }
        try {
            new org.openjdk.jmh.runner.Runner(builder.build()).run();
        } catch (RunnerException e) {
            throw new RuntimeException(e);
        }
    }

}
