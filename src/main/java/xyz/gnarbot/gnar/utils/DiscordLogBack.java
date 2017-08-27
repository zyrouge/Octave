package xyz.gnarbot.gnar.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;
import xyz.gnarbot.gnar.Bot;

public class DiscordLogBack extends AppenderBase<ILoggingEvent> {
    private static boolean enabled;

    private PatternLayout patternLayout;

    public static void disable() {
        enabled = false;
    }

    public static void enable() {
        enabled = true;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!enabled) return;
        String consoleWebhook = Bot.KEYS.getConsoleWebhook();
        if (consoleWebhook == null) return;

        if (!event.getLevel().isGreaterOrEqual(Level.INFO)) return;

        String content = patternLayout.doLayout(event);

        if (content.length() > 2000) {
            StringBuilder sb = new StringBuilder(":warning: Received a message but it was too long. ");

            String url = Utils.hasteBin(content);
            sb.append(url != null ? url : "Error while posting to HasteBin.");

            content = sb.toString();
        }

        Request request = new Request.Builder()
                .url(consoleWebhook)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, new JSONObject().put("content", content).toString()))
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(HttpUtils.EMPTY_CALLBACK);
    }

    @Override
    public void start() {
        patternLayout = new PatternLayout();
        patternLayout.setContext(getContext());
        patternLayout.setPattern("`%d{HH:mm:ss}` `%t/%level` `%logger{0}` %msg");
        patternLayout.start();

        super.start();
    }
}