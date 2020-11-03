package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/2
 */

public class LiveLock {
    private final static Logger logger = LoggerFactory.getLogger(LiveLock.class);

    static int count = 10;

    static final Object object = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (count > 0) {
                sleep(100);
                count--;
                logger.debug("{}", count);
            }
            logger.debug("{}", count);
        }, "t1");

        Thread t2 = new Thread(() -> {
            while (count < 20) {
                sleep(100);
                count++;
                logger.debug("{}", count);
            }
            logger.debug("{}", count);
        }, "t2");
        t1.start();
        t2.start();
    }
}
