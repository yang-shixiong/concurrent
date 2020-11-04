package com.yang.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class CyclicBarrierDemo {
    private static final Logger logger = LoggerFactory.getLogger(CyclicBarrierDemo.class);

    private static final CyclicBarrier barrier = new CyclicBarrier(2);

    public static void main(String[] args) {

        new Thread(() -> {
            logger.debug("start");
            try {
                barrier.await();
                logger.debug("end");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {
            logger.debug("start");
            try {
                sleep(3000);
                barrier.await();
                logger.debug("end");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }
}
