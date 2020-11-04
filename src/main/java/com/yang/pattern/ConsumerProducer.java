package com.yang.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class ConsumerProducer {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerProducer.class);

    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue(2);
        for (int i = 0; i < 4; i++) {
            int id = i;
            new Thread(() -> {
                logger.debug("downing....");
                sleep(1000);
                messageQueue.put(new Message(id, "content" + id));
            }, "producer-" + i).start();
        }

        new Thread(() -> {
            while (true){
                Message take = messageQueue.take();
                logger.debug("get message: {}, {}", take.getId(), take.getMessage());
            }
        }, "consumer").start();
    }
}

class MessageQueue {
    private static final Logger logger = LoggerFactory.getLogger(MessageQueue.class);

    private final LinkedList<Message> queue;

    private final int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
        queue = new LinkedList<>();
    }

    public Message take() {
        synchronized (queue) {
            while (queue.isEmpty()) {
                logger.debug("queue is empty");
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = queue.removeFirst();
            queue.notifyAll();
            return message;
        }
    }

    public void put(Message message) {
        synchronized (queue) {
            while (queue.size() == capacity) {
                logger.debug("queue is full");
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(message);
            queue.notifyAll();
        }
    }
}

class Message {
    private final int id;

    private final Object message;

    public Message(int id, Object message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public Object getMessage() {
        return message;
    }
}
