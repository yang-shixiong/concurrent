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

public class DeadLock {
    private final static Logger logger = LoggerFactory.getLogger(DeadLock.class);

    public static final Object objA = new Object();

    public static final Object objB = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized(objA){
                logger.debug("lock A");
                sleep(100);
                synchronized(objB){
                    logger.debug("lock B");
                }
            }
        }).start();

        new Thread(() -> {
            synchronized(objB){
                logger.debug("lock B");
                sleep(50);
                synchronized(objA){
                    logger.debug("lock A");
                }
            }
        }).start();
    }
}
