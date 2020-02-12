package xyz.gnarbot.gnar.utils;

import kotlin.Pair;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class defines a rate-limit that will be taken into account
 * when a key of type {@link K} is checked against this rate-limiter.
 *
 * @param <K> Key type.
 */
public class RateLimiter<K> {
    /**
     * Executor service of which the rate-limiter uses
     * to remove the time-out for each key.
     */
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Backing map of which the rate-limiter keeps track
     * of the keys.
     */
    private final Map<K, Pair<AtomicInteger, AtomicLong>> map = new ConcurrentHashMap<>();

    /**
     * The number of checks allowed before rate-limited status.
     */
    private final int threshold;

    /**
     * How long the key will be timed out for.
     */
    private final long timeout;

    /**
     * Constructs a rate-limiter where the amount of checks
     * before triggering rate-limited status is 1, along with
     * pre-defined timeout amount and unit.
     *
     * @param amount Amount of the duration, in terms of the unit.
     * @param unit   Unit that the duration is measured in.
     */
    public RateLimiter(long amount, TimeUnit unit) {
        this(1, amount, unit);
    }

    /**
     * Constructs a rate-limiter with defined checks threshold
     * along with pre-defined timeout duration instance.
     *
     * @param threshold Checks allowed before rate-limit status.
     * @param duration  Duration instance, converted to milliseconds.
     */
    public RateLimiter(int threshold, Duration duration) {
        this(threshold, duration.toMillis());
    }

    /**
     * Constructs a rate-limiter with defined checks threshold
     * along with pre-defined timeout amount and unit.
     *
     * @param threshold Checks allowed before rate-limit status.
     * @param amount    Amount of the duration, in terms of the unit.
     * @param unit      Unit that the duration is measured in.
     */
    public RateLimiter(int threshold, long amount, TimeUnit unit) {
        this(threshold, unit.toMillis(amount));
    }

    /**
     * Constructs a rate-limiter with defined checks threshold
     * along with pre-defined timeout value measured in milliseconds.
     *
     * @param threshold Checks allowed before rate-limit status.
     * @param timeout   Timeout duration in milliseconds.
     */
    public RateLimiter(int threshold, long timeout) {
        this.threshold = threshold;
        this.timeout = timeout;
    }

    /**
     * Checks if the key is rate-limited.
     * <p>
     * The number of checks allowed before rate-limited status is defined
     * by the {@link #threshold}. How long the key will be timed out for
     * is defined by the {@link #timeout}.
     *
     * @param key The key to check if it is rate-limited.
     * @return {@code true} if the key is not rate-limited.
     * {@code false} if the key is rate-limited.
     */
    public boolean check(final K key) {
        // Pair ( count to check against threshold, time until rate-limit entry is cleared )
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

    /**
     * Checks the remaining time until rate-limit for the key is cleared.
     *
     * @param key The key to check for remaining time.
     * @return The remaining time of which the key's rate-limit entry is cleared.
     */
    public long remainingTime(final K key) {
        Pair<AtomicInteger, AtomicLong> entry = map.get(key);
        return entry == null ? 0 : entry.getSecond().get() - System.currentTimeMillis();
    }
}
