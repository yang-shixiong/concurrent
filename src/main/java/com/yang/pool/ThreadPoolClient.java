package com.yang.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class ThreadPoolClient {
    private static final  Logger logger = LoggerFactory.getLogger(ThreadPoolClient.class);

    public static void main(String[] args) {
//        logger.debug("==========================take put==========================");
//        ThreadPoolClient.demo(() -> new ThreadPool(1, 1));
//        logger.debug("==========================take tryPut==========================");
//        ThreadPoolClient.demo(() -> new ThreadPool((queue, task) -> {}, 1, 1));
//        logger.debug("==========================offer poll==========================");
//        ThreadPoolClient.demo(() -> new ThreadPool(1000, TimeUnit.MILLISECONDS, 1, 1));
        logger.debug("==========================offer tryPut==========================");
        ThreadPoolClient.demo(() -> new ThreadPool(1000, TimeUnit.MILLISECONDS, (queue, task) -> Thread.dumpStack(),1, 1));
    }

    public static void demo(Supplier<ThreadPool> supplier){
        ThreadPool threadPool = supplier.get();
        for (int i = 0; i < 10; i++) {
            int id = i;
            threadPool.execute(() -> {
                sleep(1000);
                logger.debug("id: {}", id);
            });
        }
    }
}
