package xyz.gnarbot.gnar.utils;

import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Utils {
    public static Consumer<Message> deleteMessage(final int seconds) {
        return msg -> msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
    }

    public static String[] stringSplit(String s) {
        List<String> f = new ArrayList<>();

        int p = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                f.add(s.substring(p, i));
                p = i + 1;
                while (i < s.length() - 1 && s.charAt(i + 1) == ' ') {
                    i++;
                    p++;
                }
            } else if (s.charAt(i) == '`') {
                if (s.substring(i, Math.min(i + 3, s.length())).equals("```")) {
                    int start = i + 3;
                    i += 3;
                    while (i < s.length() - 3 && !s.substring(i, Math.min(i + 3, s.length())).equals("```")) {
                        i++;
                    }
                    int end = i;
                    f.add(s.substring(start, end));
                    i += 3;
                    p = i;
                }
            } else if (s.charAt(i) == '\n'
                    || s.charAt(i) == '\r' && s.charAt(i + 1) == '\n') {
                f.add(s.substring(p, i + 1));
                p = i + 1;
            }
        }
        f.add(s.substring(p));

        return f.toArray(new String[f.size()]);
    }

    public static String getTimestamp(long milliseconds) {
        long seconds = milliseconds / 1000 % 60;
        long minutes = milliseconds / (1000 * 60) % 60;
        long hours = milliseconds / (1000 * 60 * 60) % 24;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
