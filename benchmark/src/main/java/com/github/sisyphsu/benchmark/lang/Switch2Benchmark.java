package com.github.sisyphsu.benchmark.lang;

import com.github.sisyphsu.benchmark.Runner;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Benchmark                        Mode  Cnt  Score   Error  Units
 * Switch2Benchmark.createByIfElse  avgt    6  0.269 ± 0.007  ns/op
 * Switch2Benchmark.createBySwitch  avgt    6  2.441 ± 0.148  ns/op
 *
 * 通过map实现switch存在许多调用栈中断，性能不如ifelse
 *
 * @author sulin
 * @since 2019-07-24 12:30:41
 */
@SuppressWarnings("all")
@Warmup(iterations = 2, time = 2)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class Switch2Benchmark {

    /**
     * Create collection for benchmark
     */
    static int idByIfElse(Object obj) {
        if (obj == null) {
            return 0;
        } else if (obj instanceof Boolean) {
            return 1;
        } else if (obj instanceof Float) {
            return 2;
        } else if (obj instanceof Double) {
            return 3;
        } else if (obj instanceof Long) {
            return 4;
        } else if (obj instanceof Byte) {
            return 5;
        } else if (obj instanceof Short) {
            return 6;
        } else if (obj instanceof Integer) {
            return 7;
        } else if (obj instanceof Character) {
            return 8;
        } else if (obj instanceof String) {
            return 9;
        } else if (obj instanceof Enum) {
            return 10;
        } else if (obj instanceof char[]) {
            return 11;
        } else if (obj instanceof boolean[]) {
            return 12;
        } else if (obj instanceof byte[]) {
            return 13;
        } else if (obj instanceof short[]) {
            return 14;
        } else if (obj instanceof int[]) {
            return 15;
        } else if (obj instanceof long[]) {
            return 16;
        } else if (obj instanceof float[]) {
            return 17;
        } else if (obj instanceof double[]) {
            return 19;
        } else if (obj instanceof Object[]) {
            return 20;
        } else if (obj instanceof List) {
            return 21;
        } else {
            return 23;
        }
    }

    private final static Creator             NULL        = (t) -> 0;
    private final static Map<Class, Creator> CREATORS    = new ConcurrentHashMap<>();
    private final static Map<Class, Creator> CREATOR_MAP = new ConcurrentHashMap<>();

    static {
        // List
        CREATORS.put(Boolean.class, (t) -> 1);
        CREATORS.put(Byte.class, (t) -> 1);
        CREATORS.put(Short.class, (t) -> 1);
        CREATORS.put(Integer.class, (t) -> 1);
        CREATORS.put(Long.class, (t) -> 1);
        CREATORS.put(Float.class, (t) -> 1);
        CREATORS.put(Double.class, (t) -> 1);
        CREATORS.put(Character.class, (t) -> 1);
        CREATORS.put(String.class, (t) -> 1);
        CREATORS.put(Enum.class, (t) -> 1);

        CREATORS.put(boolean[].class, (t) -> 1);
        CREATORS.put(byte[].class, (t) -> 1);
        CREATORS.put(short[].class, (t) -> 1);
        CREATORS.put(int[].class, (t) -> 1);
        CREATORS.put(long[].class, (t) -> 1);
        CREATORS.put(float[].class, (t) -> 1);
        CREATORS.put(double[].class, (t) -> 1);
        CREATORS.put(char[].class, (t) -> 1);

        CREATORS.put(Object[].class, (t) -> 1);
        CREATORS.put(List.class, (t) -> 1);
    }

    /**
     * Create an collection instance by the specified Type.
     */
    protected static int idBySwitch(Object obj) {
        Class<?> cls = obj.getClass();
        Creator creator = CREATOR_MAP.get(cls);
        if (creator == null) {
            creator = NULL;
            for (Map.Entry<Class, Creator> entry : CREATORS.entrySet()) {
                if (cls == (entry.getKey())) {
                    creator = entry.getValue();
                }
            }
            CREATOR_MAP.put(cls, creator);
        }
        if (creator == NULL) {
            throw new IllegalArgumentException("Unsupported Type: " + cls);
        }
        return creator.create(obj);
    }

    @FunctionalInterface
    private interface Creator {
        int create(Object obj);
    }

    @Benchmark
    public void createBySwitch() {
        idBySwitch(new double[0]);
    }

    @Benchmark
    public void createByIfElse() {
        idByIfElse(new double[0]);
    }

    public static void main(String[] args) {
        idBySwitch(new double[0]);
        idByIfElse(new double[0]);
        Runner.run(Switch2Benchmark.class);
    }

}