package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/3
 */

public final class SingleTon implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SingleTon.class);
    private static final SingleTon INSTANCE = new SingleTon();

    private SingleTon() {
    }

    public static SingleTon getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        HungrySingleton2.getInstance();
    }

    public Object readResolve() {
        return INSTANCE;
    }
}

final class HungrySingleton implements Serializable {
    private static volatile HungrySingleton INSTANCE;

    private HungrySingleton() {
    }

    public static HungrySingleton getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (HungrySingleton.class) {
            if (INSTANCE != null) {
                return INSTANCE;
            }
            INSTANCE = new HungrySingleton();
            return INSTANCE;
        }
    }
}

final class HungrySingleton2 {
    private static volatile HungrySingleton2 INSTANCE;

    private HungrySingleton2() {
    }

    public static HungrySingleton2 getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        public static HungrySingleton2 INSTANCE = new HungrySingleton2();
    }
}
