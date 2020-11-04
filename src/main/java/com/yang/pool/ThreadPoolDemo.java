package com.yang.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class ThreadPoolDemo {
    private static final  Logger logger = LoggerFactory.getLogger(ThreadPoolDemo.class);

    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newCachedThreadPool(new Factory());
//        executorService.execute(() -> logger.debug("1234"));

        List<Callable<Integer>> callables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int id = i;
            callables.add(() -> {
                logger.debug("{}", id);
                Thread.sleep(10000);
                return id;
            });
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1,
                TimeUnit.MINUTES, new SynchronousQueue<>(), new Factory(), (r, executor) -> Thread.dumpStack());
        try {



            logger.debug("-------------------------------------------");
            Integer integer = threadPoolExecutor.invokeAny(callables);
            logger.debug("----:{}", integer);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
}

class Factory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    Factory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "pool-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
