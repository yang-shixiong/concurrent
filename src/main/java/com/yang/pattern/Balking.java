package com.yang.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class Balking {
    private static final Logger logger = LoggerFactory.getLogger(Balking.class);

    public static Balking INSTANCE;

    public static Balking getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (Balking.class) {
            if (INSTANCE != null) {
                return INSTANCE;
            }
            INSTANCE = new Balking();
            return INSTANCE;
        }
    }
}
