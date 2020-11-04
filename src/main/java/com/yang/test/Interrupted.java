package com.yang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/10/31
 */
public class Interrupted {
    private static final Logger logger = LoggerFactory.getLogger(Interrupted.class);

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true) {
                Thread thread = Thread.currentThread();
                boolean interrupted = thread.isInterrupted();
                if (interrupted) {
                    logger.debug("the interrupt: {}", interrupted);
                    break;
                }
            }
        }, "t1");
        t1.start();
        sleep(100);
        t1.interrupt();
    }
}
