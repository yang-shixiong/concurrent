package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/3
 */
public class HappensBefore {
    static final Object object = new Object();
    private static final Logger logger = LoggerFactory.getLogger(HappensBefore.class);
    static int num;

    static int x;

    static int y;

    static boolean run = true;

    public static void main(String[] args) {
//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
        test6();
    }

    /**
     * 对基本类型默认值的写，对于其他线程该变量读可见。并且具备传递性， x -> hb y, y -> hb z, x -> hb z
     * 因为为防止指令重排，因此一般会配合volatile
     */
    private static void test6() {
        for (int i = 0; i < 1000000; i++) {
            int id = i;
            int m = i + 1;
            new Thread(() -> {
                y = m;
                x = id;
            }, "t1").start();

            new Thread(() -> {
                if (y - x != 1) {
                    logger.debug("error, x:{}, y: {}", x, y);
                }
            }, "t2").start();
        }
    }

    /**
     * 当前线程打断其他线程前的写对于其他线程以及得知其他线程被打断的线程的变量读可见
     */
    private static void test5() {
        Thread t1 = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
            }
            logger.debug("x: {}", x);
        }, "t1");

        Thread t2 = new Thread(() -> {
            LockSupport.park();
            x = 50;
            t1.interrupt();
        }, "t2");

        t1.start();
        t2.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LockSupport.unpark(t2);
        while (!t1.isInterrupted()) {
            Thread.yield();
        }
        logger.debug("x: {}", x);
    }

    /**
     * 线程结束前对变量的写，对于其他线程得知他结束后的读可见
     */
    private static void test4() {
        Thread t1 = new Thread(() -> {
            x = 30;
            LockSupport.park();
            logger.debug("end");
        }, "t1");
        t1.start();
        LockSupport.unpark(t1);
//        try {
//            t1.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        logger.debug("x: {}", x);
        while (t1.isAlive()) {
        }
        logger.debug("x: {}", x);
    }

    /**
     * 线程start之前变量的写，对于该线程启动之后变量读可见
     */
    private static void test3() {
        Thread t1 = new Thread(() -> {
            logger.debug("x:{}", x);
        }, "t1");
        x = 20;
        t1.start();
    }

    /**
     * volatile变量的写对于其他线程可见
     */
    private static void test2() {
        new Thread(() -> {
            x = 10;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.debug("t1 end");
        }, "t1").start();
        LockSupport.parkNanos(10000);
        new Thread(() -> {
            logger.debug("x: {}", x);
        }, "t2").start();
    }

    /**
     * 线程解锁之前对变量的写，对于其他线程可见
     */
    private static void test1() {
        new Thread(() -> {
            synchronized (object) {
                num = 10;
                run = false;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.debug("t1 end");
            }
        }, "t1").start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            logger.debug("{}", num);
        }, "t2").start();

        while (run) {

        }
        logger.debug("end, {}", run);
    }
}
