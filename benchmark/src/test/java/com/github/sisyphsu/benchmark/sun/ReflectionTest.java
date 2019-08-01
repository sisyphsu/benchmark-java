package com.github.sisyphsu.benchmark.sun;

import org.junit.Test;
import sun.reflect.MethodAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Method;

/**
 * @author sulin
 * @since 2019-07-30 13:48:00
 */
public class ReflectionTest {

    public static void log() {
        System.out.println("test");
    }

    @Test
    public void test() throws Exception {
        Method method = ReflectionTest.class.getMethod("log");

        MethodAccessor accessor = ReflectionFactory.getReflectionFactory().newMethodAccessor(method);

        accessor.invoke(null, null);
    }

}
