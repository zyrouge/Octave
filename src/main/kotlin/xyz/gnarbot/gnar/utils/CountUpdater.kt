package xyz.gnarbot.gnar.utils

import okhttp3.*
import org.json.JSONObject
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.Shard
import java.io.IOException

class CountUpdater(val shard: Shard) {
    companion object {
        val CLIENT: OkHttpClient = OkHttpClient.Builder().build()
    }

    fun update() {
        Bot.LOG.info("Sending shard updates for shard ${shard.id}")
        updateCarbonitex()
        updateAbal()
        updateDiscordBots()
    }

    private fun updateDiscordBots() {
        if (Bot.KEYS.discordBots == null) return

        val json =  JSONObject()
                .put("server_count", shard.jda.guilds.size)
                .put("shard_id", shard.id)
                .put("shard_count", Bot.KEYS.botShards)

        val request = Request.Builder()
                .url("https://discordbots.org/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", Bot.KEYS.discordBots)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
                .build()

        CLIENT.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Bot.LOG.error("DiscordBots update failed for shard ${shard.id}: ${e.message}")
                call.cancel()
            }

            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        })
    }

    private fun updateAbal() {
        if (Bot.KEYS.abal == null) return

        val json =  JSONObject()
                .put("server_count", shard.jda.guilds.size)
                .put("shard_id", shard.id)
                .put("shard_count", Bot.KEYS.botShards)

        val request = Request.Builder()
                .url("https://bots.discord.pw/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", Bot.KEYS.abal)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
                .build()

        CLIENT.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Bot.LOG.error("Abal update failed for shard ${shard.id}: ${e.message}")
                call.cancel()
            }

            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        })
    }

    private fun updateCarbonitex() {
        if (Bot.KEYS.abal == null) return
        if (Bot.KEYS.carbonitex == null) return

        val json = JSONObject()
                .put("key", Bot.KEYS.carbonitex)
                .put("shardid", shard.id)
                .put("shardcount", Bot.KEYS.botShards)
                .put("servercount", shard.jda.guilds.size)

        val request = Request.Builder()
                .url("https://www.carbonitex.net/discord/data/botdata.php")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", Bot.KEYS.abal)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
                .build()

        CLIENT.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Bot.LOG.error("Carbonitex update failed for shard ${shard.id}: ${e.message}")
                call.cancel()
            }

            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        })
    }
}