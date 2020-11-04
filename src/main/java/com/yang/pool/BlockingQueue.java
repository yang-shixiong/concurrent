package com.yang.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class BlockingQueue<T> {
    private static final Logger logger = LoggerFactory.getLogger(BlockingQueue.class);

    /**
     * 任务队列
     */
    private final Deque<T> queue = new ArrayDeque<>();

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 队列已满等待条件室
     */
    private final Condition fullWaitSet = lock.newCondition();

    /**
     * 队列为空等待条件室
     */
    private final Condition emptyWaitSet = lock.newCondition();

    /**
     * 队列容量
     */
    private final int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 超市获取
     *
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @return 任务
     */
    public T poll(long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.isEmpty()) {
                if (nanos <= 0) {
                    logger.debug("time out, exist, not get task");
                    return null;
                }
                try {
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 一直等待获取
     *
     * @return 任务
     */
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    logger.debug("take, wait task");
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加任务
     *
     * @param task 任务
     */
    public void put(T task) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    logger.debug("queue full, always wait, task: {}", task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(task);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加吗如果超时则返回
     *
     * @param task     任务
     * @param timeout  时间
     * @param timeUnit 时间单位
     * @return 成功返回ture，否则false
     */
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capacity) {
                if (nanos <= 0) {
                    logger.debug("time out, not add, task: {}", task);
                    return false;
                }
                try {
                    logger.debug("queue full, wait: {}, task: {}", nanos, task);
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 尝试添加如果任务已满，就执行拒绝策略
     *
     * @param task         任务
     * @param rejectPolicy 拒绝策略
     */
    public void tryPut(T task, RejectPolicy<T> rejectPolicy) {
        lock.lock();
        try {
            if (queue.size() == capacity) {
                logger.debug("queue full, add fail, task: {}", task);
                rejectPolicy.reject(this, task);
            } else {
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
