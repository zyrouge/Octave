package xyz.gnarbot.gnar.tests;

import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;
import org.junit.Test;
import xyz.gnarbot.gnar.Credentials;

public class UnirestPosting {
    @Test
    public void unirestUpdate() throws Exception {
        int i = 25000;

        String auth = Credentials.ABAL;
        String key = Credentials.CARBONITEX;

        JSONObject json = new JSONObject()
                .put("key", key)
                .put("servercount", i);

        String response = Unirest.post("https://www.carbonitex.net/discord/data/botdata.php")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", auth)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(json)
                .asString().getStatusText();

        System.out.println(response);
    }

    @Test
    public void updateDiscordBotsCount() throws Exception {
        int i = 25000;

        String auth = Credentials.DISCORDBOTS;

        JSONObject json = new JSONObject().put("server_count", i);

        String response = Unirest.post("https://discordbots.org/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", auth)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(json)
                .asString()
                .getStatusText();

        System.out.println(response);
    }

    @Test
    public void unirestUpdate2() throws Exception {
        int i = 25000;

        String auth = Credentials.ABAL;

        JSONObject json = new JSONObject()
                .put("server_count", i);

        String response = Unirest.post("https://bots.discord.pw/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", auth)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(json)
                .asString().getStatusText();

        System.out.println(response);
    }
}
