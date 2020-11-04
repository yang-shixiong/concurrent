package com.yang.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class SemaphoreDemo {
    private static final  Logger logger = LoggerFactory.getLogger(SemaphoreDemo.class);

    private static final  Semaphore lock = new Semaphore(3);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    lock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try{
                    logger.debug("running....");
                    sleep(1000);
                    logger.debug("end...");
                }finally {
                    lock.release();
                }
            }, "t-" + i).start();
        }

        logger.debug("finished?");
    }
}
