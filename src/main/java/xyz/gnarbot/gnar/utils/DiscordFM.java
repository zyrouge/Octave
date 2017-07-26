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

    private static Map<String, List<String>> libraries = new HashMap<>();

    public static void loadLibraries() {
        for (String lib : LIBRARIES) {
            String discordFMKey = Bot.KEYS.getDiscordFM();
            String url;

            try {
                url = new URIBuilder("https://temp.discord.fm/libraries/" + lib + "/musicbot").toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                continue;
            }

            Request.Builder rb = new Request.Builder()
                    .url(url)
                    .header("Accept", "application/json");

            if (discordFMKey != null) {
                rb.header("Authorization", discordFMKey);
            }

            HttpUtils.CLIENT.newCall(rb.build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ResponseBody body = response.body();
                    if (body != null) {
                        List<String> strings = Arrays.stream(body.string().split("\n"))
                                .parallel()
                                .filter(s -> s.startsWith("https://")) // make sure hte line starts with a valid link
                                .collect(Collectors.toList());
                        libraries.put(lib, strings);
                    }
                }
            });
        }
    }

    public static String getRandomSong(String library) {
        List<String> urls = libraries.get(library);
        return urls.get((int) (Math.random() * urls.size()));
    }
}
