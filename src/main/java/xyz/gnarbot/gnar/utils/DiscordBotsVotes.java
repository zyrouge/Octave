package xyz.gnarbot.gnar.utils;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONTokener;
import xyz.gnarbot.gnar.Bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiscordBotsVotes {
    private static final List<String> voteIDs = new ArrayList<>();

    static {
        String auth = Bot.KEYS.getDiscordBots();
        if (auth != null) {
            Request request = new Request.Builder()
                    .url("https://discordbots.org/api/bots/201503408652419073/votes?onlyids=true")
                    .addHeader("Authorization", auth)
                    .build();

            Bot.EXECUTOR.scheduleAtFixedRate(() -> {
                try (Response response = HttpUtils.CLIENT.newCall(request).execute()) {
                    ResponseBody body = response.body();
                    if (body == null) return;

                    JSONArray jsa = new JSONArray(new JSONTokener(body.byteStream()));
                    voteIDs.clear();

                    for (int i = 0; i < jsa.length(); i++) {
                        voteIDs.add(jsa.getString(i));
                    }
                } catch (IOException ignore) {}
            }, 1, 10, TimeUnit.MINUTES);
        }
    }

    public static List<String> getVoteIDs() {
        return voteIDs;
    }
}
