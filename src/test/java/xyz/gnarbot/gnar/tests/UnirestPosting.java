package xyz.gnarbot.gnar.tests;

import okhttp3.*;
import org.json.JSONObject;
import org.junit.Test;
import xyz.gnarbot.gnar.Credentials;
import xyz.gnarbot.gnar.Requester;

import java.io.IOException;

public class UnirestPosting {
    @Test
    public void update() {
        int count = 32500;

        updateAbalCount(count);
        updateCarbonitexCount(count);
        updateDiscordBotsCount(count);
    }

    private void updateAbalCount(int i) {
        JSONObject json = new JSONObject().put("server_count", i);

        Request request = new Request.Builder()
                .url("https://bots.discord.pw/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", Credentials.ABAL)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(Requester.JSON, json.toString()))
                .build();

//        try (Response response = Requester.CLIENT.newCall(request).execute()) {
//            System.out.println("Abal | Response code: " + response.code());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Requester.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.code());
            }
        });
    }

    private void updateDiscordBotsCount(int i) {
        JSONObject json = new JSONObject().put("server_count", i);

        Request request = new Request.Builder()
                .url("https://discordbots.org/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", Credentials.DISCORDBOTS)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(Requester.JSON, json.toString()))
                .build();

        Requester.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.code());
            }
        });
    }

    private void updateCarbonitexCount(int i) {
        JSONObject json = new JSONObject().put("key", Credentials.CARBONITEX).put("servercount", i);

        Request request = new Request.Builder()
                .url("https://www.carbonitex.net/discord/data/botdata.php")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", Credentials.ABAL)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(Requester.JSON, json.toString()))
                .build();

        Requester.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.code());
            }
        });
    }
}
