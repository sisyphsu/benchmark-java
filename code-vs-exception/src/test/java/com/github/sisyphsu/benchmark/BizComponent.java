package com.github.sisyphsu.benchmark;

/**
 * 模拟业务组件
 *
 * @author sulin
 * @since 2019-03-10 13:27:40
 */
public class BizComponent {

    /**
     * 模拟异常错误码, 直接返回错误码
     */
    public BizResult<Long> mockResult() {
        long time = System.currentTimeMillis();
        return new BizResult<Long>(10, "error", time);
    }

    /**
     * 模拟异常业务, 内部直接抛出业务异常
     */
    public Long mockException(int depth) {
        return deepCall(depth, 1);
    }

    Long deepCall(int target, int level) {
        if (target > level) {
            return this.deepCall(target, level + 1);
        }
        long time = System.currentTimeMillis();
        if (time > 1000) {
            throw new BizException(10, "error");
        }
        return time;
    }

}
