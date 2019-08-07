package com.github.sisyphsu.benchmark.lang;

import com.imptest.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author sulin
 * @since 2019-08-07 12:05:27
 */
@Slf4j
public class TestImport {

    @Test
    public void test() {
        log.info("step1");
        try {
            MathUtil.addAndCheck(1, 2);
        } catch (Throwable e) {
            System.out.println(e.getClass());
            assert e instanceof NoClassDefFoundError;
        }
        log.info("step2");
    }

}
