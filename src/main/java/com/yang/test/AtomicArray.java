package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/3
 */

public class AtomicArray {
    private final static Logger logger = LoggerFactory.getLogger(AtomicArray.class);

    public static void main(String[] args) {
        demo(() -> new int[10], (arr) -> arr.length, (arr, index) -> arr[index]++,
                (arr) -> logger.debug("unsafe {}", Arrays.toString(arr)));

        demo(() -> new AtomicIntegerArray(10), AtomicIntegerArray::length, AtomicIntegerArray::getAndIncrement,
                (arr) -> logger.debug("safe {}", arr));
    }

    private static <T> void demo(Supplier<T> supplier, Function<T, Integer> function,
                                 BiConsumer<T, Integer> putConsumer, Consumer<T> printConsumer) {
        List<Thread> list = new ArrayList<>();
        T t = supplier.get();
        int length = function.apply(t);
        for (int i = 0; i < length; i++) {
            list.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    putConsumer.accept(t, j % length);
                }
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
        printConsumer.accept(t);
    }
}
