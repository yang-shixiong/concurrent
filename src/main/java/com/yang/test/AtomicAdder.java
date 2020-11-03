package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/3
 */

public class AtomicAdder {
    private final static Logger logger = LoggerFactory.getLogger(AtomicAdder.class);

    public static void main(String[] args) {
        for (int i = 0; i < 4; i++) {
            demo(() -> new AtomicLong(0L), AtomicLong::getAndIncrement);
        }

        for (int i = 0; i < 4; i++) {
            demo(LongAdder::new, LongAdder::increment);
        }
    }

    public static <T> void demo(Supplier<T> supplier, Consumer<T> consumer){
        T addr = supplier.get();
        List<Thread> list = new ArrayList<>();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 40; i++) {
            list.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    consumer.accept(addr);
                }
            }));
        }
        list.forEach(Thread::start);
        list.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        logger.debug("use time: {}, num: {}", System.currentTimeMillis() - l, addr);
    }
}
