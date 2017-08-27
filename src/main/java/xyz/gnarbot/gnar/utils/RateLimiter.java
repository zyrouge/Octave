package xyz.gnarbot.gnar.utils;

import gnu.trove.TCollections;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import kotlin.Pair;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimiter {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final TLongObjectMap<Pair<AtomicInteger, AtomicLong>> map = TCollections.synchronizedMap(new TLongObjectHashMap<>());

    private final int threshold;
    private final long timeout;

    public RateLimiter(long timeout, TimeUnit timeUnit) {
        this(1, timeout, timeUnit);
    }

    public RateLimiter(int threshold, long timeout, TimeUnit timeUnit) {
        this.threshold = threshold;
        this.timeout = timeUnit.toMillis(timeout);
    }

    public boolean check(final long key) {
        Pair<AtomicInteger, AtomicLong> entry = map.get(key);
        if (entry == null) {
            entry = new Pair<>(new AtomicInteger(), new AtomicLong());
            map.put(key, entry);
        }

        AtomicInteger count = entry.getFirst();
        if (count.get() >= threshold) {
            return false;
        }
        count.incrementAndGet();

        entry.getSecond().set(System.currentTimeMillis() + timeout);

        executor.schedule(() -> {
            if (count.decrementAndGet() <= 0) {
                map.remove(key);
            }
        }, timeout, TimeUnit.MILLISECONDS);

        return true;
    }

    public long remainingTime(final long key) {
        Pair<AtomicInteger, AtomicLong> entry = map.get(key);
        return entry == null ? 0 : entry.getSecond().get() - System.currentTimeMillis();
    }
}
