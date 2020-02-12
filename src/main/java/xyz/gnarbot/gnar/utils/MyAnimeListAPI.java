package xyz.gnarbot.gnar.utils;

import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.json.XML;
import xyz.gnarbot.gnar.Bot;

import java.io.IOException;
import java.net.URISyntaxException;

public class MyAnimeListAPI {
    public static final String SEARCH_ANIME = "anime/search.xml";
    public static final String SEARCH_MANGA = "manga/search.xml";
    private static final String API_PREFIX = "https://myanimelist.net/api/";
    private final String username;
    private final String password;
    private boolean login = false;

    public MyAnimeListAPI(String username, String password) {
        if (username == null || password == null) {
            Bot.getLogger().error("No MyAnimeListAPI credentials provided.");
            login = false;
        } else {
            Bot.getLogger().info("Attempting to log in.");
            Request request = new Request.Builder().url(API_PREFIX + "account/verify_credentials.xml")
                    .header("Authorization", Credentials.basic(username, password))
                    .build();
            try (Response response = HttpUtils.CLIENT.newCall(request).execute()) {
                ResponseBody body = response.body();
                if (body != null) {
                    JSONObject jso = XML.toJSONObject(body.string());
                    response.close();
                    if (jso.has("user") && jso.getJSONObject("user").has("id")) {
                        Bot.getLogger().info("Logged in to MyAnimeListAPI.");
                        login = true;
                    }
                } else {
                    Bot.getLogger().error("Response was null when logging in to MyAnimeListAPI");
                }
            } catch (IOException e) {
                Bot.getLogger().info("Error logging in to MyAnimeListAPI.", e);
                login = false;
            }
        }
        this.username = username;
        this.password = password;
    }

    public boolean isLoggedIn() {
        return login;
    }

    public JSONObject makeRequest(String target, String query) {
        if (isLoggedIn()) {
            String url;
            try {
                url = new URIBuilder(API_PREFIX + target).addParameter("q", query).toString();
            } catch (URISyntaxException e) {
                return null;
            }

            Request request = new Request.Builder().url(url)
                    .header("Authorization", Credentials.basic(username, password))
                    .build();
            try (Response response = HttpUtils.CLIENT.newCall(request).execute()) {
                ResponseBody body = response.body();
                if (body == null) return null;

                JSONObject jso = XML.toJSONObject(body.string());
                response.close();
                return jso;
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}