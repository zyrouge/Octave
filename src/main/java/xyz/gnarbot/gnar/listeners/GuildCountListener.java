package xyz.gnarbot.gnar.listeners;

import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.*;
import org.json.JSONObject;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;

public class GuildCountListener extends ListenerAdapter {
    private int changes = 0;

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

        for (Shard shard : Bot.getShards()) {
            count += shard.getJda().getGuilds().size();
        }

        updateAbalCount(count);
        updateCarbonitexCount(count);
        updateDiscordBotsCount(count);
    }

    private void updateAbalCount(int i) {
        if (Bot.KEYS.getAbal() == null) return;

        JSONObject json = new JSONObject().put("server_count", i);

        Request request = new Request.Builder()
                .url("https://bots.discord.pw/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", Bot.KEYS.getAbal())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bot.LOG.error("Abal failed.", e);
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bot.LOG.info("Abal | " + response.code());
                response.close();
            }
        });
    }

    private void updateDiscordBotsCount(int i) {
        if (Bot.KEYS.getDiscordBots() == null) return;

        JSONObject json = new JSONObject().put("server_count", i);

        Request request = new Request.Builder()
                .url("https://discordbots.org/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", Bot.KEYS.getDiscordBots())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bot.LOG.error("DiscordBots failed.", e);
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bot.LOG.info("DiscordBots | " + response.code());
                response.close();
            }
        });
    }

    private void updateCarbonitexCount(int i) {
        if (Bot.KEYS.getAbal() == null) return;
        if (Bot.KEYS.getCarbonitex() == null) return;

        JSONObject json = new JSONObject().put("key", Bot.KEYS.getCarbonitex()).put("servercount", i);

        Request request = new Request.Builder()
                .url("https://www.carbonitex.net/discord/data/botdata.php")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", Bot.KEYS.getAbal())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bot.LOG.error("Carbonitex failed.", e);
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bot.LOG.info("Carbonitex | " + response.code());
                response.close();
            }
        });
    }
}
