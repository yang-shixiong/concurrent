package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */
public class ThreadUnSafe {
    private static final  Logger logger = LoggerFactory.getLogger(ThreadUnSafe.class);

    static ArrayList<String> list = new ArrayList<>();


    public static void main(String[] args) {
        ThreadUnSafe threadUnSafe = new ThreadUnSafe();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> threadUnSafe.method1(200), "thread-" + i).start();
        }
    }

    public void method1(int loopNumber){
//        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < loopNumber; i++) {
            method2(list);
            method3(list);
        }
    }

    public void method2(ArrayList<String> list){
        list.add("1");
    }

    public void method3(ArrayList<String> list){
        list.remove(0);
    }
}
