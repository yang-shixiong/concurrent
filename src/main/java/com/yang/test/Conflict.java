package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */
public class Conflict {
    private static final  Logger logger = LoggerFactory.getLogger(Conflict.class);

    public static int count;
    public static int count2;
    public static volatile int vv;
    public static volatile int vv2;

    public static final Object condition = new Object();

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            test(i);
        }
        logger.debug("test use time: {}", System.currentTimeMillis() - start);

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            test2(i);
        }
        logger.debug("test2 use time: {}", System.currentTimeMillis() - start2);
    }

    private static void test(int j) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (condition) {
                    count++;
                    vv++;
                }
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (condition) {
                    count--;
                    vv--;
                }
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        logger.debug("{}, count: {}, vv: {}", j, count, vv);
    }

    private static void test2(int j) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                count2++;
                vv2++;
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                count2--;
                vv2--;
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        logger.debug("{}, count2: {}, vv2: {}", j, count2, vv2);
    }
}
