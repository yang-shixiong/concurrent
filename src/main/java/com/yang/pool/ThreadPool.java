package com.yang.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class ThreadPool {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPool.class);
    /**
     * 任务队列
     */
    private final BlockingQueue<Runnable> queue;
    /**
     * 工作线程集合
     */
    private final HashSet<Worker> workers = new HashSet<>();
    /**
     * 核心线程数量
     */
    private final int coreSize;
    /**
     * 超时时间
     */
    private long timeout;
    /**
     * 超时时间单位
     */
    private TimeUnit timeUnit;
    /**
     * 拒绝策略
     */
    private RejectPolicy<Runnable> rejectPolicy;

    /**
     * 线程构造函数，调用队列的take以及put方法
     *
     * @param queueCapacity 任务队列容量
     * @param coreSize      核心线程
     */
    public ThreadPool(int queueCapacity, int coreSize) {
        this.queue = new BlockingQueue<>(queueCapacity);
        this.coreSize = coreSize;
    }

    /**
     * 线程构造函数，增加拒绝策略，使用任务队列的take以及tryPut
     *
     * @param rejectPolicy  拒绝策略
     * @param queueCapacity 任务队列容量
     * @param coreSize      核心线程
     */
    public ThreadPool(RejectPolicy<Runnable> rejectPolicy, int queueCapacity, int coreSize) {
        this(queueCapacity, coreSize);
        this.rejectPolicy = rejectPolicy;
    }

    /**
     * 带超时时间的，调用任务队列的poll以及offer
     *
     * @param timeout       超时时间
     * @param timeUnit      超时时间单位
     * @param queueCapacity 队列容量
     * @param coreSize      核心线程
     */
    public ThreadPool(long timeout, TimeUnit timeUnit, int queueCapacity, int coreSize) {
        this(queueCapacity, coreSize);
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    /**
     * 带超时时间以及拒绝策略，调用tryPut以及poll
     *
     * @param timeout       超时时间
     * @param timeUnit      超时时间单位
     * @param rejectPolicy  拒绝策略
     * @param queueCapacity 任务队列容量
     * @param coreSize      核心线程
     */
    public ThreadPool(long timeout, TimeUnit timeUnit, RejectPolicy<Runnable> rejectPolicy, int queueCapacity, int coreSize) {
        this(rejectPolicy, queueCapacity, coreSize);
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void execute(Runnable task) {
        synchronized (this) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                workers.add(worker);
                worker.start();
            } else {
                if (rejectPolicy != null) {
                    queue.tryPut(task, rejectPolicy);
                } else {
                    if (timeout == 0) {
                        queue.put(task);
                    } else {
                        queue.offer(task, timeout, timeUnit);
                    }
                }
            }
        }
    }

    /**
     * 工作线程，继承Thread
     */
    private class Worker extends Thread {

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        /**
         * 重写run方法，根据逻辑触发操作
         */
        @Override
        public void run() {

            while (task != null || (task = timeout == 0 ? queue.take() : queue.poll(timeout, timeUnit)) != null) {
                logger.debug("execute task: {}", task);
                task.run();
                task = null;
                synchronized (workers) {
                    logger.debug("remove worker: {}", this);
                    workers.remove(this);
                }
            }
        }
    }
}
