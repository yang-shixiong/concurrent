package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/2
 */

public class ExerciseSell {
    private final static Logger logger = LoggerFactory.getLogger(ExerciseSell.class);

    static Random random = new Random();

    public static int randomAmount(){
        return random.nextInt(5) +1;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            test();
        }
    }

    private static void test() {
        TicketWindow ticketWindow = new TicketWindow(2000);
        List<Thread> list = new ArrayList<>();
//        List<Integer> count = new ArrayList<>();
        List<Integer> count = new Vector<>();
        for (int i = 0; i < 2000; i++) {
            Thread t = new Thread(() -> {
                int sell = ticketWindow.sell(randomAmount());
                count.add(sell);
            }, "t" + i);
            list.add(t);
            t.start();

        }
        list.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        logger.debug("sell count: {}", count.stream().mapToInt(i -> i).sum());
        logger.debug("current ticket: {}", ticketWindow.getCount());
    }
}

class TicketWindow{
    public int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    public int getCount(){
        return count;
    }

    public int sell(int amount){
        if(count >= amount){
            this.count -= amount;
            return amount;
        }else {
            int count = this.count;
            this.count = 0;
            return count;
        }
    }
}
