package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/3
 */
public class NotExistLoop {
    private static final  Logger logger = LoggerFactory.getLogger(NotExistLoop.class);

    static boolean run = true;
//    static volatile boolean run = true;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            run = false;
            logger.debug("modify");
        }, "t1").start();
        while (run){
//            System.out.println(run);
        }
        logger.debug("end");
    }

}
