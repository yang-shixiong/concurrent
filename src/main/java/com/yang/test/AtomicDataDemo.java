package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class AtomicDataDemo {
    private static final Logger logger = LoggerFactory.getLogger(AtomicDataDemo.class);

    public static void main(String[] args) {
        AtomicData atomicData = new AtomicData(10000);
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(new Thread(() -> {
                atomicData.decrease(1);
            }));
        }
        list.forEach(Thread::start);
        for (Thread thread : list) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.debug("data: {}", atomicData.getData());
    }
}

class AtomicData {
    static final Unsafe unsafe;
    static final long DATA_OFFSET;

    static {
        unsafe = UnsafeAccessor.getUnsafe();

        try {
            DATA_OFFSET = unsafe.objectFieldOffset(AtomicData.class.getDeclaredField("data"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("error");
        }
    }

    private final int data;

    public AtomicData(int data) {
        this.data = data;
    }

    public void decrease(int amount) {
//        data -= amount;
        while (true) {
            if (unsafe.compareAndSwapInt(this, DATA_OFFSET, data, data - amount)) {
                break;
            }
        }
    }

    public int getData() {
        return data;
    }
}

class UnsafeAccessor {
    private static Unsafe unsafe;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }
}