package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/3
 */
public class FiledUpdater {
    private static final Logger logger = LoggerFactory.getLogger(FiledUpdater.class);

    private volatile int field = 5;

    public static void main(String[] args) {
        AtomicIntegerFieldUpdater<FiledUpdater> field = AtomicIntegerFieldUpdater.newUpdater(FiledUpdater.class, "field");
        FiledUpdater filedUpdater = new FiledUpdater();
        boolean b1 = field.compareAndSet(filedUpdater, 5, 10);
        logger.debug("success: {}, the field: {}", b1, filedUpdater.getField());

        boolean b = field.compareAndSet(filedUpdater, 5, 10);
        logger.debug("success: {} ,the field: {}", b, filedUpdater.getField());
    }

    public int getField() {
        return field;
    }
}
