package xyz.gnarbot.gnar.listeners;

import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.*;
import org.json.JSONObject;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Credentials;
import xyz.gnarbot.gnar.Requester;
import xyz.gnarbot.gnar.Shard;

import java.io.IOException;

public class GuildCountListener extends ListenerAdapter {
    private final Bot bot;

    private int changes = 0;

    public GuildCountListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        updateQueue();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        updateQueue();
    }

    private void updateQueue() {
        changes++;

        if (changes > 20) {
            update();
            changes = 0;
        }
    }

    /**
     * Updates Server Counts on ad sites
     */
    private void update() {
        int count = 0;

        for (Shard shard : bot.getShards()) {
            count += shard.getGuilds().size();
        }

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

        Requester.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                bot.getLog().error("Abal failed.", e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bot.getLog().info("Abal | " + response.code());
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
                bot.getLog().error("DiscordBots failed.", e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bot.getLog().info("DiscordBots | " + response.code());
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
                bot.getLog().error("Carbonitex failed.", e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bot.getLog().info("Carbonitex | " + response.code());
            }
        });
    }
}
