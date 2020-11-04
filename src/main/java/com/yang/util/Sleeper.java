package com.yang.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */
public class Sleeper {
    private static final  Logger logger = LoggerFactory.getLogger(Sleeper.class);

    public static void sleep(long millis){
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
