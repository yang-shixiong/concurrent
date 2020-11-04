package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/2
 */
public class PhilosopherDemo {
    private static final  Logger logger = LoggerFactory.getLogger(PhilosopherDemo.class);

    public static void main(String[] args) {
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");

        new Philosopher(c1, c2, "1").start();
        new Philosopher(c2, c3, "2").start();
        new Philosopher(c3, c4, "3").start();
        new Philosopher(c4, c5, "4").start();
        new Philosopher(c5, c1, "5").start();
//        new Philosopher(c1, c5, "5").start();
    }
}

class Chopstick {
    String name;

    public Chopstick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Chopstick{" +
                "name='" + name + '\'' +
                '}';
    }
}

class Philosopher extends Thread {
    private static final  Logger logger = LoggerFactory.getLogger(Philosopher.class);
    final Chopstick left;

    final Chopstick right;

    String name;

    public Philosopher(Chopstick left, Chopstick right, String name) {
        this.left = left;
        this.right = right;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (left) {
                synchronized (right) {
                    logger.debug("{} eat ...", name);
                }
            }
        }
    }
}
