package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/3
 */
public class CASDemo {
    private static final  Logger logger = LoggerFactory.getLogger(CASDemo.class);

    public static void main(String[] args) {
//        Account.demo(new AccountSafe(10000));
//
//        Account.demo(new AccountUnsafe(10000));
//        Account.demo(new AccountSafe(10000));
//        Account.demo(new AccountCAS(10000));

        AtomicInteger atomicInteger = new AtomicInteger(100);
        atomicInteger.getAndAccumulate(100, Integer::sum);
        logger.debug("{}", atomicInteger.get());
        int i = 0;
        logger.debug("{}, i: {}", i == (i = 1), i);
    }
}


interface Account {
    Logger logger = LoggerFactory.getLogger(Account.class);

    Integer getBalance();

    void withdraw(Integer amount);

    static void demo(Account account) {
        List<Thread> list = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            list.add(new Thread(() -> {
                account.withdraw(10);
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
        long end = System.nanoTime();
        //  [main] - balance: 1400, use time:   69610700
        //  [main] - balance: 0, use time:      76696201
        //  [main] - balance: 0, use time:      62596499
        logger.debug("balance: {}, use time: {}", account.getBalance(), end - start);
    }
}

class AccountUnsafe implements Account {
    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        return balance;
    }

    @Override
    public void withdraw(Integer amount) {
        balance -= amount;
    }
}

class AccountSafe implements Account {
    private Integer balance;

    public AccountSafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public synchronized Integer getBalance() {
        return balance;
    }

    @Override
    public synchronized void withdraw(Integer amount) {
        balance -= amount;
    }
}

class AccountCAS implements Account {
    private final AtomicInteger balance;

    public AccountCAS(Integer balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

//    @Override
//    public void withdraw(Integer amount) {
//        while (true){
//            int prev = balance.get();
//            int next = prev - amount;
//            if(balance.compareAndSet(prev, next)){
//                break;
//            }
//        }
//    }


    @Override
    public void withdraw(Integer amount) {
        balance.addAndGet(-1 * amount);
    }
}