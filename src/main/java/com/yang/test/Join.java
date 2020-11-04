package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */
public class Join {
    private static final Logger logger = LoggerFactory.getLogger(Join.class);

    static int num;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.debug("t1 set num...");
            num = 10;
        }, "t1");
        t1.start();
        num = 6;
        t1.join(100);
        logger.debug("num: {}", num);
    }
}
