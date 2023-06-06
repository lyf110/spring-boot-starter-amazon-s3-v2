package com.amazon.s3.v2.core.functions;

/**
 * @author liuyangfang
 * @description 自定义的函数式接口
 * @since 2023/6/2 12:57:34
 */
@FunctionalInterface
public interface CustomBiFunction<T, U, V, W, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param v the third function argument
     * @param w the four function argument
     * @return the function result
     */
    R apply(T t, U u, V v, W w);
}
