package xyz.gnarbot.gnar.utils;

import okhttp3.*;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.gnarbot.gnar.Bot;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscordFM {
    public static final String[] LIBRARIES = {
            "electro-hub", "chill-corner", "korean-madness",
            "japanese-lounge", "classical", "retro-renegade",
            "metal-mix", "hip-hop", "electro-swing",
            "purely-pop", "rock-n-roll", "coffee-house-jazz"
    };

    private static Map<String, List<String>> cache = new HashMap<>(LIBRARIES.length);

    public DiscordFM(Bot bot) {
        for (String lib : LIBRARIES) {
            String discordFMKey = bot.getCredentials().getDiscordFM();
            String url;

            try {
                url = new URIBuilder("https://gnarbot.xyz/assets/dfm/" + lib + ".txt").toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                continue;
            }

            Request.Builder rb = new Request.Builder()
                    .url(url)
                    .header("Accept", "application/plain");

            if (discordFMKey != null) {
                rb.header("Authorization", discordFMKey);
            }

            Callback callback = new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    call.cancel();
                    Bot.getLogger().error("DiscordFM Error", e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ResponseBody body = response.body();
                    if (body != null) {
                        cache.put(lib, Arrays.stream(body.string().split("\n"))
                                .parallel()
                                .filter(s -> s.startsWith("https://")) // make sure hte line starts with a valid link
                                .collect(Collectors.toList()));
                    }
                    response.close();
                }
            };

            HttpUtils.CLIENT.newCall(rb.build()).enqueue(callback);
        }
    }

    public String getRandomSong(String library) {
        try {
            List<String> urls = cache.get(library);
            return urls.get((int) (Math.random() * urls.size()));
        } catch (Exception e) {
            Bot.getLogger().error("DiscordFM Error", e);
            return "https://www.youtube.com/watch?v=D7npse9n-Yw"; //Technical Difficulties video
        }
    }
}
