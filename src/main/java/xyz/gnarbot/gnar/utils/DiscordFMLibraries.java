package xyz.gnarbot.gnar.utils;

import okhttp3.*;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import xyz.gnarbot.gnar.Bot;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class DiscordFMLibraries {

    public static final String[] libraryTypes =
            {"electro-hub", "chill-corner", "korean-madness",
            "japanese-lounge", "classical", "retro-renegade",
            "metal-mix", "hip-hop", "electro-swing",
            "purely-pop", "rock-n-roll", "coffee-house-jazz"};

    private static Map<String, JSONArray> libraries = new HashMap<>();

    public static void loadLibraries() {

        for(String s : libraryTypes) {
            try {
                String url = new URIBuilder("https://temp.discord.fm/libraries/" + s + "/json")
                        .toString();
                Request request = new Request.Builder()
                        .url(url)
                        .header("Accept", "application/json")
                        .header("Authorization", Bot.KEYS.getDiscordFM())
                        .build();
                HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        libraries.put(s, new JSONArray(body.string()));
                    }
                });

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    public static String getRandomSong(String library) {
        try {
            JSONObject j = libraries.get(library).getJSONObject((int) (Math.random() * libraries.get(library).length()));

            if (j.has("url")) {
                return j.getString("url");
            } else if (j.getString("service").equals("YouTubeVideo")) {
                return "https://youtube.com/watch?v=" + j.getString("identifier");
            }

            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "ullPointerException, please report this to the devs.";
        }
    }
}
