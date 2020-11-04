package com.yang.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.StampedLock;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class StampDemo {
    private static final Logger logger = LoggerFactory.getLogger(StampDemo.class);

    public static void main(String[] args) {
        DataCenterStamp dataCenterStamp = new DataCenterStamp(1);
//        new Thread(() -> logger.debug("read: {}", dataCenterStamp.read()), "t1").start();
//        new Thread(() -> logger.debug("read: {}", dataCenterStamp.read()), "t2").start();

        new Thread(() -> logger.debug("read: {}", dataCenterStamp.read()), "t1").start();
        new Thread(() -> {
            logger.debug("write");
            dataCenterStamp.write(1);
        }, "t2").start();
        new Thread(() -> logger.debug("read: {}", dataCenterStamp.read()), "t2").start();
    }
}

class DataCenterStamp {
    private static final Logger logger = LoggerFactory.getLogger(DataCenterStamp.class);
    private final StampedLock lock = new StampedLock();
    private int data;

    public DataCenterStamp(int data) {
        this.data = data;
    }

    public int read() {
        long read = lock.tryOptimisticRead();
        sleep(1000);
        if (lock.validate(read)) {
            logger.debug("need not add read lock: {}", read);
            return data;
        }
        long stamp = lock.readLock();
        try {
            logger.debug("data change, add read lock: {}", stamp);
            return data;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public void write(int value) {
        long stamp = lock.writeLock();
        try {
            logger.debug("add write lock: {}", stamp);
            data -= value;
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
