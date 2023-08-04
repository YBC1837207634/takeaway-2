package com.example.common;


/**
 * 获取 threadLocal 存放当前线程变量，用于 MP 的自动填充
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static ThreadLocal<Long> getThreadLocal() {
        return threadLocal;
    }
}
