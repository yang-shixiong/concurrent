package com.yang.collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class CopyOnWriteArrayListDemo {
    private static final Logger logger = LoggerFactory.getLogger(CopyOnWriteArrayListDemo.class);

    public static void main(String[] args) {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();

        list.add(1);
        list.add(2);
        list.add(3);
        Iterator<Integer> iterator = list.iterator();
        new Thread(() -> {
            list.remove(0);
            logger.debug("list: {}", list);
        }).start();
        sleep(1000);
        while (iterator.hasNext()) {
            logger.debug("{}", iterator.next());
        }
    }
}
