package com.yang.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class TwoPhaseTermination {
    private static final Logger logger = LoggerFactory.getLogger(TwoPhaseTermination.class);

    private static Thread monitor = null;

    private static volatile boolean flag = true;

    private static Thread monitor2 = null;

    public static void main(String[] args) {
        isInterrupted();
        useFlag();
        sleep(3000);
        monitor.interrupt();
        monitor2.interrupt();
        flag = false;
    }

    public static void isInterrupted(){
        monitor = new Thread(() -> {
            while (true) {
                Thread thread = Thread.currentThread();
                if (thread.isInterrupted()) {
                    logger.debug("thread is interrupt");
                    break;
                }
                try {
                    Thread.sleep(2000);
                    logger.debug("doing");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    thread.interrupt();
                }
            }
        }, "monitor");
        monitor.start();
    }

    public static void useFlag(){
        monitor2 = new Thread(() -> {
            while (flag){
                Thread thread = Thread.currentThread();
                try {
                    Thread.sleep(2000);
                    logger.debug("doing");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    thread.interrupt();
                }
            }
            logger.debug("thread is interrupt");
        }, "monitor2");
        monitor2.start();
    }
}
