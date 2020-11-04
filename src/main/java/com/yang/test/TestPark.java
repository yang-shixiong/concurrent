package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/2
 */
public class TestPark {
    private static final  Logger logger = LoggerFactory.getLogger(TestPark.class);

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            logger.debug("start");
            LockSupport.unpark(Thread.currentThread());
            LockSupport.park();
            logger.debug("are parked?");
        }, "t1");
        t1.start();
    }
}
