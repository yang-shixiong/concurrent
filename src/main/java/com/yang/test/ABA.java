package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.LockSupport;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/3
 */
public class ABA {
    private static final  Logger logger = LoggerFactory.getLogger(ABA.class);

    static AtomicReference<String> ref = new AtomicReference<>("A");
    static AtomicStampedReference<String> ref2 = new AtomicStampedReference<>("A", 0);

    public static void main(String[] args) {
        logger.debug("main.....");
//        test1();
        int stamp = ref2.getStamp();
        Thread t1 = new Thread(() -> {
            logger.debug("try to update from {} to B, stamp:{}", ref2.getReference(), ref2.getStamp());
            ref2.compareAndSet(ref.get(), "B", ref2.getStamp(), ref2.getStamp() + 1);
        }, "t1");
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread t2 = new Thread(() -> {
            logger.debug("try to update from {} to A, stamp:{}", ref2.getReference(), ref2.getStamp());
            ref2.compareAndSet(ref.get(), "A", ref2.getStamp(), ref2.getStamp() + 1);
        }, "t3");
        t2.start();
        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.debug("pre stamen: {}, current stamp: {}", stamp, ref2.getStamp());
        logger.debug("try to update fromA to B");
        boolean b = ref2.compareAndSet("A", "B", 0, 1);
        logger.debug("is success:{}, current: {}, stamp: {}", b, ref2.getReference(), ref2.getStamp());
    }

    private static void test1() {
        Thread thread = Thread.currentThread();
        Thread t1 = new Thread(() -> {
            logger.debug("try to set from {} to B", ref.get());
            ref.compareAndSet(ref.get(), "B");
        }, "t1");
        Thread t2 = new Thread(() -> {
            LockSupport.park();
            logger.debug("try to set from {} to A", ref.get());
            ref.compareAndSet(ref.get(), "A");
            LockSupport.unpark(thread);
        }, "t2");
        t1.start();
        t2.start();
        while (t1.isAlive()){
            Thread.yield();
        }
        LockSupport.unpark(t2);
        LockSupport.park();
        logger.debug("main thread try to update from A to B, current: {}", ref.get());
        ref.compareAndSet("A", "B");
        logger.debug("end ref: {}", ref.get());
    }
}
