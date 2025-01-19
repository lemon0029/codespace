package com.example.demo.insert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RandomDataProvider {

    public static String string() {
        return UUID.randomUUID().toString();
    }

    public static LocalDateTime localDateTime() {
        long t1 = System.currentTimeMillis();
        long t2 = t1 - 30 * 24 * 60 * 60 * 1000L;

        long t3 = ThreadLocalRandom.current().nextLong(t2, t1);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(t3), ZoneId.systemDefault());
    }


}
