package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/2
 */
public class ReentrantLockDemo {
    private static final  Logger logger = LoggerFactory.getLogger(ReentrantLockDemo.class);

    static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
//        method1();
        testInterrupt();
    }

    private static void testInterrupt() {
        Thread thread = new Thread(() -> {

            try {
//                lock.lock();
                lock.lockInterruptibly();
                logger.debug("lock");
            } catch (Exception e) {
                logger.debug("interrupt");
                e.printStackTrace();
                lock.unlock();

            }
        });
        lock.lock();
        try{
            thread.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.interrupt();
            logger.debug("end");
        }finally {
            lock.unlock();
        }
    }

    private static void method1() {
        new Thread(() -> {
            lock.lock();
            try {
                logger.debug("method1");
                method2();
            } finally {
                lock.unlock();
            }
        }, "t1").start();
    }

    private static void method2() {
        new Thread(() -> {
            lock.lock();
            try {
                logger.debug("method2");
                method3();
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }

    private static void method3() {
        lock.lock();
        try {
            logger.debug("method3");
            method4();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.debug("interrupt: {}", Thread.currentThread().isInterrupted());
            }
        } finally {
            logger.debug("method3 release lock");
            lock.unlock();
        }
    }

    private static void method4() {
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            if(!lock.tryLock()){
                logger.debug("try to interrupt...");
                thread.interrupt();
                try {
                    if(!lock.tryLock(506, TimeUnit.MILLISECONDS)){
                        logger.debug("not get the lock");
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                logger.debug("method4");
            } finally {
                lock.unlock();
            }
        }, "t4").start();
    }
}
