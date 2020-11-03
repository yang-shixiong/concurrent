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
    private final static Logger logger = LoggerFactory.getLogger(SingleTon.class);

    private SingleTon() {
    }

    private static final SingleTon INSTANCE = new SingleTon();

    public static SingleTon getInstance() {
        return INSTANCE;
    }

    public Object readResolve() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        HungrySingleton2.getInstance();
    }
}

final class HungrySingleton implements Serializable {
    private HungrySingleton() {
    }

    private static volatile HungrySingleton INSTANCE;

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
    private HungrySingleton2() {
    }

    private static volatile HungrySingleton2 INSTANCE;

    private static class LazyHolder {
        public static HungrySingleton2 INSTANCE = new HungrySingleton2();
    }

    public static HungrySingleton2 getInstance() {
        return LazyHolder.INSTANCE;
    }
}
