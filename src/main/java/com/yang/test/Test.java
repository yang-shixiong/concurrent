package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */

public abstract class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        new Tests().bar();
    }

    public void bar() {
        SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        foo(format);
    }

    protected abstract void foo(SimpleDateFormat format);
}

class Tests extends Test {
    @Override
    protected void foo(SimpleDateFormat format) {
        String dateStr = "1990-10-11 00:00:00";
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    format.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
