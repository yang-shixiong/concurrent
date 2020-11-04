package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/2
 */
public class LockCondition {
    private static final  Logger logger = LoggerFactory.getLogger(LockCondition.class);

    static ReentrantLock lock = new ReentrantLock();

    static Condition takeOutCondition = lock.newCondition();

    static Condition condition = lock.newCondition();

    static boolean isTakeOut = false;
    static boolean cigarette = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            lock.lock();
            try{
                while (!cigarette){
                    try {
                        logger.debug("wait cigarette");
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.debug("start working...");
            }finally {
                lock.unlock();
            }
        }, "t1").start();

        new Thread(()->{
            lock.lock();
            try{
                while (!isTakeOut){
                    try {
                        logger.debug("wait takeout");
                        takeOutCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.debug("start working...");
            }finally {
                lock.unlock();
            }
        }, "t2").start();

        Thread.sleep(5000);
        lock.lock();
        try{
            cigarette = true;
            condition.signalAll();
        }finally {
            lock.unlock();
        }

        Thread.sleep(5000);
        lock.lock();
        try{
            isTakeOut = true;
            takeOutCondition.signalAll();
        }finally {
            lock.unlock();
        }
    }
}
