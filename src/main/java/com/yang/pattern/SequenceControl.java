package com.yang.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:
 * a,b,c各输出五次
 *
 * @author mark
 * Date 2020/11/4
 */
public class SequenceControl {
    private static final Logger logger = LoggerFactory.getLogger(SequenceControl.class);

    public static void main(String[] args) {
//        SyncWaitNotify syncWaitNotify = new SyncWaitNotify(1, 5);
//        new Thread(() -> syncWaitNotify.print(1, 2, "a"), "t1").start();
//        new Thread(() -> syncWaitNotify.print(2, 3, "b"), "t2").start();
//        new Thread(() -> syncWaitNotify.print(3, 1, "c"), "t3").start();

//        awaitSingle awaitSingle = new awaitSingle(5);
//        Condition conditionA = awaitSingle.newCondition();
//        Condition conditionB = awaitSingle.newCondition();
//        Condition conditionC = awaitSingle.newCondition();
//
//        new Thread(() -> awaitSingle.print("a", conditionA, conditionB), "ta").start();
//        new Thread(() -> awaitSingle.print("b", conditionB, conditionC), "tb").start();
//        new Thread(() -> awaitSingle.print("c", conditionC, conditionA), "tc").start();
//        awaitSingle.start(conditionA);

        SyncPark syncPark = new SyncPark(5);
        Thread a = new Thread(() -> syncPark.print("a"), "a");
        Thread b = new Thread(() -> syncPark.print("b"), "b");
        Thread c = new Thread(() -> syncPark.print("c"), "c");
        syncPark.setThreads(new Thread[]{a, b, c});
        syncPark.start();
    }
}

class SyncWaitNotify {
    private static final Logger logger = LoggerFactory.getLogger(SyncWaitNotify.class);

    private int flag;

    private int loopNumber;

    public SyncWaitNotify(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }

    public void print(int waitFlag, int nextFlag, String str) {
        for (int i = 0; i < loopNumber; i++) {
            synchronized (this) {
                while (waitFlag != this.flag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.debug("{}", str);
                flag = nextFlag;
                this.notifyAll();
            }
        }
    }
}

class awaitSingle extends ReentrantLock {
    private static final Logger logger = LoggerFactory.getLogger(awaitSingle.class);

    private final int loopNumber;

    public awaitSingle(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void start(Condition first) {
        this.lock();
        try {
            logger.debug("start");
            first.signal();
        } finally {
            this.unlock();
        }
    }

    public void print(String str, Condition current, Condition next) {
        for (int i = 0; i < loopNumber; i++) {
            this.lock();
            try {
                current.await();
                logger.debug("{}", str);
                next.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.unlock();
            }
        }
    }
}

class SyncPark {
    private static final Logger logger = LoggerFactory.getLogger(SyncPark.class);

    private final int loopNumber;

    private Thread[] threads;

    public SyncPark(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void setThreads(Thread[] threads) {
        this.threads = threads;
    }

    public void print(String str) {
        for (int i = 0; i < loopNumber; i++) {
            LockSupport.park();
            logger.debug(str);
            LockSupport.unpark(nextThread());
        }
    }

    public void start(){
        for (Thread thread : threads) {
            thread.start();
        }
        LockSupport.unpark(threads[0]);
    }

    private Thread nextThread() {
        int index = 0;
        for (int i = 0; i < threads.length; i++) {
            if (threads[i] == Thread.currentThread()) {
                index = i;
                break;
            }
        }
        if (index < threads.length - 1) {
            return threads[index + 1];
        } else {
            return threads[0];
        }
    }
}