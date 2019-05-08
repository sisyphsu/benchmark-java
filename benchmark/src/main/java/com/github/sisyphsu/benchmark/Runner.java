package com.github.sisyphsu.benchmark;

import com.github.sisyphsu.benchmark.common.MemoryProfiler;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author sulin
 * @since 2019-05-08 13:18:27
 */
public class Runner {

    public static void run(Class cls) throws Exception {
        Options opt = new OptionsBuilder()
                .include(cls.getSimpleName())
                .addProfiler(GCProfiler.class)
                .addProfiler(MemoryProfiler.class)
                .build();

        new org.openjdk.jmh.runner.Runner(opt).run();
    }

}
