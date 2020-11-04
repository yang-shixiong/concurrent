package com.yang.collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Description:
 *
 * @author mark
 * Date 2020/11/4
 */
public class MapTest {
    private static final Logger logger = LoggerFactory.getLogger(MapTest.class);

    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";

    private static final int count = 200;

    public static void main(String[] args) {
        logger.debug("start HashMap");
        demo(() -> new HashMap<String, Integer>(),
                (map, list) -> {
                    for (String s : list) {
                        int i = map.get(s) == null ? 1 : map.get(s) + 1;
                        map.put(s, i);
                    }
                });

        logger.debug("start ConcurrentHashMap merge");
        demo(() -> new ConcurrentHashMap<String, Integer>(),
                (map, list) -> {
                    for (String s : list) {
                        map.merge(s, 1, Integer::sum);
                    }
                });

        logger.debug("start ConcurrentHashMap computeIfAbsent");
        demo(() -> new ConcurrentHashMap<String, LongAdder>(),
                (map, list) -> {
                    for (String s : list) {
                        map.computeIfAbsent(s, k -> new LongAdder()).increment();
                    }
                });

    }

    private static <T> void demo(Supplier<Map<String, T>> supplier, BiConsumer<Map<String, T>, List<String>> consumer) {
        create();
        Map<String, T> counterMap = supplier.get();
        List<Thread> list = new ArrayList<>();

        for (int i = 0; i < 26; i++) {
            int id = i;
            list.add(new Thread(() -> {
                List<String> strings = readFromFile(id);
                consumer.accept(counterMap, strings);
            }));
        }
        list.forEach(Thread::start);
        for (Thread thread : list) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.debug("{}", counterMap);
    }

    private static List<String> readFromFile(int i) {
        List<String> list = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\tmp\\" + (i + 1) + ".txt")))) {
            while (true) {
                String s = in.readLine();
                if (s == null) {
                    break;
                }
                list.add(s);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void create() {
        File file = new File("E:\\tmp\\1.txt");
        if (file.exists()) {
            return;
        }

        List<String> list = new ArrayList<>(ALPHA.length() * count);
        for (int i = 0; i < ALPHA.length(); i++) {
            char ch = ALPHA.charAt(i);
            for (int j = 0; j < count; j++) {
                list.add(String.valueOf(ch));
            }
        }
        Collections.shuffle(list);
        for (int i = 0; i < 26; i++) {
            try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream("E:\\tmp\\" + (i + 1) + ".txt")))) {
                String collect = String.join("\n", list.subList(i * count, (i + 1) * count));
                out.print(collect);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
