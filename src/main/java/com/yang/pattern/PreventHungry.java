package com.yang.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Description:
 * <p>
 * 不同的线程做不同的事
 *
 * @author mark
 * Date 2020/11/4
 */
public class PreventHungry {
    private static final Logger logger = LoggerFactory.getLogger(PreventHungry.class);

    private static final List<String> MENU = Arrays.asList("apple", "banana", "orange", "peanut");

    private static final Random random = new Random();

    public static String cooking() {
        return MENU.get(random.nextInt(MENU.size()));
    }

    public static void main(String[] args) {
        ExecutorService waiterService = Executors.newFixedThreadPool(1);
        ExecutorService cookerService = Executors.newFixedThreadPool(1);

        for (int i = 0; i < 3; i++) {
            waiterService.execute(() -> {
                logger.debug("handle order");
                Future<String> cooking = cookerService.submit(() -> {
                    logger.debug("start cooking");
                    return cooking();
                });
                try {
                    logger.debug("eat: {}", cooking.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
