package com.novibe.common.util;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimiter {

    private final AtomicLong lastRequestTime = new AtomicLong(0);
    private final long minIntervalMillis = 1000; // 1 запрос в секунду для NextDNS

    @SneakyThrows
    public void acquire() {
        long now = System.currentTimeMillis();
        long lastTime = lastRequestTime.get();
        long waitTime = minIntervalMillis - (now - lastTime);

        if (waitTime > 0) {
            TimeUnit.MILLISECONDS.sleep(waitTime);
        }
        lastRequestTime.set(System.currentTimeMillis());
    }
}
