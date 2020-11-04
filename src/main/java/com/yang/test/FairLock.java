package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/2
 */
public class FairLock {
    private static final Logger logger = LoggerFactory.getLogger(FairLock.class);

    public static ReentrantLock noFairLock = new ReentrantLock();

    public static ReentrantLock fairLock = new ReentrantLock(true);

    public static void main(String[] args) {
        for (int i = 0; i < 500; i++) {
            new Thread(() -> {
                noFairLock.lock();
                try {
                    logger.debug("running...");
                } finally {
                    noFairLock.unlock();
                }
            }, "t" + (i + 1)).start();
        }

        new Thread(() -> {
            noFairLock.lock();
            try {
                logger.debug("running...");
            } finally {
                noFairLock.unlock();
            }
        }, "insert").start();
    }
}
