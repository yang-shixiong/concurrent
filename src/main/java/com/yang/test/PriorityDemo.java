package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */

public class PriorityDemo {
    private final static Logger logger = LoggerFactory.getLogger(PriorityDemo.class);

    static int count1;
    static int count2;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (; ; ) {
                count1++;
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (; ; ) {
                count2++;
            }
        }, "t2");
        t1.setPriority(Thread.MAX_PRIORITY);
        t1.setPriority(Thread.MIN_PRIORITY);
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.debug("count1: {}", count1);
        logger.debug("count2: {}", count2);
    }
}
