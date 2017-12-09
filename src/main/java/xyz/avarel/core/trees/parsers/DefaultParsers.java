package xyz.avarel.core.trees.parsers;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultParsers {
    public static final Parser<?, String> STRING = new Parser<>("string", "Plain text", (c, s) -> s);

    public static final Parser<?, Integer> INTEGER = new Parser<>("integer", "Integer number", (c, s) -> {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    });

    public static final Parser<?, Double> DECIMAL = new Parser<>("number", "Number, can be decimal", (c, s) -> {
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    });

    private static final Pattern durationPattern = Pattern.compile("^(?:(?:(\\d+):)?(\\d+):)?(\\d+)$");
    public static final Parser<?, Duration> DURATION = new Parser<>("hh:mm:ss", "Timestamp", (c, s) -> {
        Matcher m = durationPattern.matcher(s);

        if (!m.find()) {
            return null;
        }

        String[] group = {m.group(1), m.group(2), m.group(3)};
        long hours = group[0] == null ? 0 : Long.parseLong(group[0]);
        long minutes = group[1] == null ? 0 : Long.parseLong(group[1]);
        long seconds = group[2] == null ? 0 : Long.parseLong(group[2]);

        minutes += hours * 60;
        seconds += minutes * 60;

        return Duration.ofSeconds(seconds);
    });
}
