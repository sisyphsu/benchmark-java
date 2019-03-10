package com.github.sisyphsu.benchmark;

/**
 * API异常，即业务中断异常
 *
 * @author sulin
 * @since 2019-03-10 13:26:06
 */
public class BizException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;
    /**
     * 错误信息
     */
    private final String msg;

    public BizException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
