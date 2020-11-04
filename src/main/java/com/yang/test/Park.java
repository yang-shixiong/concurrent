package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */
public class Park {
    private static final Logger logger = LoggerFactory.getLogger(Park.class);

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                logger.debug("park");
                LockSupport.park();
                logger.debug("unpark");
//                logger.debug("the interrupted: {}", Thread.currentThread().isInterrupted());
                logger.debug("the interrupted: {}", Thread.interrupted());
            }
        });
        thread.start();
        sleep(1000);
        thread.interrupt();
    }
}
