package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */
public class FutureTaskDemo {
    private static final  Logger logger = LoggerFactory.getLogger(FutureTaskDemo.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            logger.debug("start...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 100;
        });
//        futureTask.run();
        Thread t1 = new Thread(futureTask, "t1");
        t1.start();
        t1.interrupt();
        logger.debug("{}", futureTask.get());
    }
}
