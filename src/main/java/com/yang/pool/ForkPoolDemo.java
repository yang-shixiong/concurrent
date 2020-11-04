package com.yang.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class ForkPoolDemo {
    private static final Logger logger = LoggerFactory.getLogger(ForkPoolDemo.class);

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new MyTask(0, 100));
    }
}

class MyTask extends RecursiveTask<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(MyTask.class);

    private final int begin;
    private final int end;

    public MyTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (begin == end) {
            logger.debug("join: {}", begin);
            return begin;
        }

        if (end - begin == 1) {
            logger.debug("join: {} + {} = {}", begin, end, begin + end);
            return begin + end;
        }
        int pivot = (begin + end) / 2;

        MyTask t1 = new MyTask(begin, pivot);
        t1.fork();
        MyTask t2 = new MyTask(pivot + 1, end);
        t2.fork();

        int result = t1.join() + t2.join();

        logger.debug("get result: {} by: {} + {}", result, t1, t2);

        return result;
    }

    @Override
    public String toString() {
        return "MyTask{" +
                "begin=" + begin +
                ", end=" + end +
                '}';
    }
}
