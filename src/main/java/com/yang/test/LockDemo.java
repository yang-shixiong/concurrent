package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */
public class LockDemo {
    private static final Logger logger = LoggerFactory.getLogger(LockDemo.class);

    public static void main(String[] args) {
        lock();
    }

    public static void lock() {
        Number number = new Number();
        new Thread(number::a).start();
        new Thread(number::b).start();
    }

}

class Number {
    private static final Logger logger = LoggerFactory.getLogger(LockDemo.class);

    public synchronized void a() {
        sleep(1000);
        logger.debug("a");
    }

    public synchronized void b() {
        logger.debug("b");
    }
}
