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
public class TestWait {
    private static final  Logger logger = LoggerFactory.getLogger(TestWait.class);

    static final  Object obj = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized(obj){
                logger.debug("start...");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.debug("exec");
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized(obj){
                logger.debug("start...");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.debug("exec");
            }
        }, "t2").start();

        sleep(1000);
        synchronized (obj){
//            obj.notify();
            obj.notifyAll();
        }
    }
}
