package com.yang.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.yang.util.Sleeper.sleep;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class ProtectivePause {
    private static final Logger logger = LoggerFactory.getLogger(ProtectivePause.class);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Person().start();
        }
        sleep(1000);
        for (Integer id : MailBoxes.getIds()) {
            new PostMan(id, "content: " + id).start();
        }
    }
}

class PostMan extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ProtectivePause.class);

    private final int personId;

    private final String mail;

    public PostMan(int personId, String mail) {
        this.personId = personId;
        this.mail = mail;
    }

    @Override
    public void run() {
        GuardedObject guardedObject = MailBoxes.getGuardedObject(personId);
        logger.debug("send mail, id:{}, mail: {}", personId, mail);
        guardedObject.complete(mail);
    }
}

class Person extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Person.class);

    @Override
    public void run() {
        GuardedObject guardedObject = MailBoxes.createGuardedObject();
        logger.debug("start receive mail: {}", guardedObject.getId());
        Object mail = guardedObject.get(5000);
        logger.debug("received email, id: {} content: {}", guardedObject.getId(), mail);
    }
}

class MailBoxes {
    private static final Map<Integer, GuardedObject> boxes = new Hashtable<>();

    private static final AtomicInteger id = new AtomicInteger(1);

    public static GuardedObject getGuardedObject(int id) {
        return boxes.get(id);
    }

    public static GuardedObject createGuardedObject() {
        GuardedObject guardedObject = new GuardedObject(id.getAndIncrement());
        boxes.put(guardedObject.getId(), guardedObject);
        return guardedObject;
    }

    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}

class GuardedObject {
    private final int id;

    public GuardedObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private Object response;

    public Object get(long time) {
        synchronized (this) {
            long start = System.currentTimeMillis();
            long passedTime = 0;
            while (response == null) {
                long waitTime = time - passedTime;
                if (waitTime <= 0) {
                    break;
                }
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passedTime = System.currentTimeMillis() - start;
            }
            return response;
        }
    }

    public void complete(Object response) {
        synchronized (this) {
            this.response = response;
            this.notifyAll();
        }
    }
}
