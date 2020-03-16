package xyz.gnarbot.gnar.commands.music

import okhttp3.Request
import org.json.JSONObject
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description
import xyz.gnarbot.gnar.utils.HttpUtils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Command(
        aliases = ["lyrics"],
        usage = "(current|search <term>)",
        description = "Look up lyrics of the current playing song or of a song of your choice."
)
@BotInfo(
        id = 193,
        category = Category.MUSIC,
        scope = Scope.VOICE
)

class LyricsCommand : CommandTemplate() {
    @Description("Lyrics of the current song.")
    fun current(context: Context) {
        val manager = context.bot.players.getExisting(context.guild)
        if(manager == null) {
            context.send().info("There's no player to be seen here.");
            return
        }

        val audioTrack = manager.player.playingTrack;
        if(audioTrack == null) {
            context.send().info("There's no song playing currently.");
            return
        }

        val title = audioTrack.info.title
        val data : JSONObject = getSongData(title)
        if(!data.isNull("error")) {
            context.send().info("No lyrics found for $title. Try with another song?")
            return
        }

        val lyrics = data.getString("content")
        val songObject = data.getJSONObject("song")
        val fullTitle = songObject.getString("full_title")

        val icon = songObject.getString("icon")

        context.send().embed("Lyrics for $fullTitle") {
            thumbnail { icon }
            desc { lyrics }
            footer { "Service provided by https://lyrics.tsu.sh. Thanks for using Octave!" }
        }.action().queue()
    }

    @Description("Searches lyrics.")
    fun search(context: Context, content: String) {
        val data : JSONObject = getSongData(content)
        if(!data.isNull("error")) {
            context.send().info("No lyrics found for $content. Try with another song?")
            return
        }

        val lyrics = data.getString("content")
        val songObject = data.getJSONObject("song")

        val fullTitle = songObject.getString("full_title")
        val icon = songObject.getString("icon")

        context.send().embed("Lyrics for $fullTitle") {
            thumbnail { icon }
            desc { lyrics }
            footer { "Service provided by https://lyrics.tsu.sh. Thanks for using Octave!" }
        }.action().queue()
    }
}

fun getSongData(content: String): JSONObject {
    val request: Request = Request.Builder()
            .url("https://lyrics.tsu.sh/v1/?q=${URLEncoder.encode(content, StandardCharsets.UTF_8.toString())}")
            .addHeader("User-Agent", "Octave")
            .build()

    HttpUtils.CLIENT.newCall(request).execute().use { response -> return JSONObject(response.body()?.string()) }
}