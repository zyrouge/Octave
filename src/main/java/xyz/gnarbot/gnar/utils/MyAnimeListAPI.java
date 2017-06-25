package xyz.gnarbot.gnar.utils;

import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.net.URISyntaxException;

import static xyz.gnarbot.gnar.Bot.LOG;

public class MyAnimeListAPI {
    private static final String API_PREFIX = "https://myanimelist.net/api/";
    public static final String SEARCH_ANIME = "anime/search.xml";
    public static final String SEARCH_MANGA = "manga/search.xml";

    private final String username;
    private final String password;
    private boolean login = false;

    public MyAnimeListAPI(String username, String password) {
        if (username == null || password == null) {
            LOG.error("No MyAnimeListAPI credentials provided.");
            login = false;
        } else {
            LOG.info("Attempting to log in.");
            Request request = new Request.Builder().url(API_PREFIX + "account/verify_credentials.xml")
                    .header("Authorization", Credentials.basic(username, password))
                    .build();
            try (Response response = HttpUtils.CLIENT.newCall(request).execute()) {
                JSONObject jso = XML.toJSONObject(response.body().string());
                response.close();
                if (jso.has("user") && jso.getJSONObject("user").has("id")) {
                    LOG.info("Logged in to MyAnimeListAPI.");
                    login = true;
                }
            } catch (IOException e) {
                LOG.info("Error logging in to MyAnimeListAPI.", e);
                login = false;
            }
        }
        this.username = username;
        this.password = password;
    }

    public boolean isLogin() {
        return login;
    }

    public JSONObject makeRequest(String target, String query) {
        if (!isLogin()) {
            return null;
        }

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
            JSONObject jso = XML.toJSONObject(response.body().string());
            response.close();
            return jso;
        } catch (IOException e) {
            return null;
        }
    }
}