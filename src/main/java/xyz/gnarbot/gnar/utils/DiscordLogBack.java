package xyz.gnarbot.gnar.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import okhttp3.*;
import org.json.JSONObject;
import xyz.gnarbot.gnar.Bot;

import java.io.IOException;

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
        if (Bot.KEYS.getConsoleWebhook() == null) return;

        if (!event.getLevel().isGreaterOrEqual(Level.INFO)) return;

        String content = patternLayout.doLayout(event);

        if (content.length() > 1920) {
            content = ":warning: Received a message but it was too long. " + Utils.hasteBin(content);
        }


        Request request = new Request.Builder()
                .url(Bot.KEYS.getConsoleWebhook())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, new JSONObject().put("content", content).toString()))
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
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