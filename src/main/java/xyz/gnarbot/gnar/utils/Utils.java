package xyz.gnarbot.gnar.utils;

import net.dv8tion.jda.core.entities.Message;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern TIME_PATTERN =
            Pattern.compile("(-?\\d+)\\s*((?:d(?:ay(?:s)?)?)|(?:h(?:our(?:s)?)?)|(?:m(?:in(?:ute(?:s)?)?)?)|(?:s(?:ec(?:ond(?:s)?)?)?))?");

    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^(\\d+)(?::(\\d+))?(?::(\\d+))?$");

    // https://regex101.com/r/VXEl27/1/
    private static final Pattern ARGUMENT_PATTERN =
            Pattern.compile("`{3}(?:\\w+\\n)?([\\s\\S]*?)`{3}|`([^`]+)`|(\\S+)");

    public static String getTime(long ms) {
        long s = ms / 1000;
        long m = s / 60;
        long h = m / 60;
        long d = h / 24;

        return d + "d " + h % 24 + "h " + m % 60 + "m " + s % 60 + "s";
    }

    public static long parseTime(String s) {
        s = s.toLowerCase();
        long ms = 0;

        Matcher matcher = TIME_PATTERN.matcher(s);
        while (matcher.find()) {
            String numStr = matcher.group(1);
            String unitStr = matcher.group(2);

            TimeUnit unit;
            if (unitStr == null) {
                unit = TimeUnit.SECONDS;
            } else switch (unitStr) {
                case "d":
                case "day":
                case "days":
                    unit = TimeUnit.DAYS;
                    break;
                case "h":
                case "hour":
                case "hours":
                    unit = TimeUnit.HOURS;
                    break;
                case "m":
                case "min":
                case "minute":
                case "minutes":
                    unit = TimeUnit.MINUTES;
                    break;
                case "s":
                case "sec":
                case "second":
                case "seconds":
                default:
                    unit = TimeUnit.SECONDS;
                    break;
            }
            ms += unit.toMillis(Long.parseLong(numStr));
        }
        return ms;
    }

    public static Consumer<Message> deleteMessage(final int seconds) {
        return msg -> msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
    }

    public static String[] stringSplit(String s) {
        List<String> parts = new ArrayList<>();

        Matcher matcher = ARGUMENT_PATTERN.matcher(s);
        outer: while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);
                if (match != null) {
                    parts.add(StringUtils.stripEnd(match, null));
                    continue outer;
                }
            }
        }

        return parts.toArray(new String[parts.size()]);
    }

    public static long parseTimestamp(String str) throws NumberFormatException {
        long seconds = 0;
        long minutes = 0;
        long hours = 0;

        Matcher m = TIMESTAMP_PATTERN.matcher(str);

        if (!m.find()) {
            throw new IllegalArgumentException(str + " is not a valid timestamp");
        }

        int capturedGroups = 0;
        if (m.group(1) != null) capturedGroups++;
        if (m.group(2) != null) capturedGroups++;
        if (m.group(3) != null) capturedGroups++;

        switch (capturedGroups) {
            case 0:
                throw new IllegalArgumentException(str + " is not a valid timestamp");
            case 1:
                seconds = Integer.parseInt(m.group(1));
                break;
            case 2:
                minutes = Integer.parseInt(m.group(1));
                seconds = Integer.parseInt(m.group(2));
                break;
            case 3:
                hours = Integer.parseInt(m.group(1));
                minutes = Integer.parseInt(m.group(2));
                seconds = Integer.parseInt(m.group(3));
                break;
        }

        minutes = minutes + hours * 60;
        seconds = seconds + minutes * 60;

        return seconds * 1000;
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

    public static String hasteBin(String content) {
        Request request = new Request.Builder().url("https://hastebin.com/documents")
                .header("User-Agent", "Gnar")
                .header("Content-Type", "text/plain")
                .post(RequestBody.create(null, content))
                .build();

        try (Response response = HttpUtils.CLIENT.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) return "Response null while posting to HasteBin.";

            JSONObject jso = new JSONObject(new JSONTokener(body.byteStream()));

            response.close();

            return "https://hastebin.com/" + jso.get("key");
        } catch (IOException e) {
            e.printStackTrace();
            return "Error posting to HasteBin.";
        }
    }

    public static String getRamMoeImage(String type) {
        try {
            OkHttpClient ok = new OkHttpClient();
            String url = new URIBuilder("https://rra.ram.moe/i/r?type="+type)
                    .toString();
            String link = "https://rra.ram.moe";
            Request request = new Request.Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .build();
            Response r = ok.newCall(request).execute();
            return link+new JSONObject (r.body().string()).getString("path");
        } catch (IOException | URISyntaxException e) {
            return "Failed to query API.";
        }
    }
}
