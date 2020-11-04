package com.yang.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class DataContainerDemo {
    private static final Logger logger = LoggerFactory.getLogger(DataContainerDemo.class);


    public static void main(String[] args) {
        DataContainer dataContainer = new DataContainer();
//
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> logger.debug("read: {}", dataContainer.read()), "t-" + i).start();
//        }
//        new Thread(() -> logger.debug("read: {}", dataContainer.read()), "t1").start();
//        new Thread(() -> {
//            dataContainer.write(1);
//            logger.debug("read: {}", dataContainer.read());
//        }, "t2").start();

        new Thread(() -> logger.debug("read: {}", dataContainer.writeAndGet(1)), "t2").start();
        new Thread(() -> logger.debug("read: {}", dataContainer.read()), "t1").start();


    }
}

class DataContainer {
    private static final Logger logger = LoggerFactory.getLogger(DataContainer.class);

    private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock.ReadLock r = rw.readLock();

    private final ReentrantReadWriteLock.WriteLock w = rw.writeLock();

    private int i = 1;

    public int read() {
        logger.debug("start get read lock");
        r.lock();
        try {
            logger.debug("start reading....");
            sleep(1000);
            return i;
        } finally {
            r.unlock();
        }
    }

    public void write(int value) {
        logger.debug("start get write lock");
        w.lock();
        try {
            logger.debug("start write");
            i += value;
        } finally {
            w.unlock();
        }
    }

    public int writeAndGet(int value) {
        logger.debug("start get write lock");
        w.lock();
        try {
            sleep(2000);
            logger.debug("start write");
            i += value;
            r.lock();
            try {
                sleep(2000);
                return i;
            } finally {
                r.unlock();
            }
        } finally {
            w.unlock();
        }
    }
}