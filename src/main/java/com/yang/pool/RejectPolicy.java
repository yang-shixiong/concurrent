package com.yang.pool;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
@FunctionalInterface
public interface RejectPolicy<T> {

    void reject(BlockingQueue<T> queue, T task);
}
