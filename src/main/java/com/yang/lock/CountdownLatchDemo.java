package com.yang.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class CountdownLatchDemo {
    private static final Logger logger = LoggerFactory.getLogger(CountdownLatchDemo.class);

    private static final CountDownLatch lock = new CountDownLatch(3);

    public static void main(String[] args) {
        // test1();

        SyncThread syncThread = new SyncThread();
        syncThread.test();
    }

    private static void test1() {
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                logger.debug("running....");
                sleep(1000);
                logger.debug("end");
                lock.countDown();
            }, "t" + i).start();
        }
        try {
            lock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.debug("finished!");
    }
}

class SyncThread {
    private final AtomicInteger num = new AtomicInteger(1);

    private final ExecutorService executorService = Executors.newFixedThreadPool(10,
            (runnable) -> new Thread(runnable, "t" + num.getAndIncrement()));

    private final CountDownLatch lock = new CountDownLatch(10);

    private final String[] all = new String[10];

    private final Random random = new Random();

    public void test() {
        for (int i = 0; i < 10; i++) {
            int id = i;
            executorService.submit(() -> {
                for (int j = 0; j < 101; j++) {
                    sleep(random.nextInt(3) * 1000);
                    all[id] = Thread.currentThread().getName() + "(" + j + "%)";
                    System.out.print("\r" + Arrays.toString(all));
                }
                lock.countDown();
            });
        }

        try {
            lock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\ngame start");
        executorService.shutdown();
    }
}
