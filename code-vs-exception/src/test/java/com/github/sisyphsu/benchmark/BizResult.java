package com.github.sisyphsu.benchmark;

/**
 * 业务结果
 *
 * @author sulin
 * @since 2019-03-10 13:30:10
 */
public class BizResult<T> {

    /**
     * 错误码
     */
    private int code;
    /**
     * 错误信息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;

    public BizResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BizResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
