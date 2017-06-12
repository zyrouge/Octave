package xyz.gnarbot.gnar.commands.executors.media

import org.json.JSONException
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.YouTube
import kotlin.collections.set

@Command(
        aliases = arrayOf("video", "vid"),
        usage = "(query...)",
        description = "Search and get a YouTube video.",
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class VideoCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Gotta put something to search YouTube.").queue()
            return
        }

        try {
            val query = args.joinToString(" ")

            val results = YouTube.search(query, 1)

            if (results.isEmpty()) {
                context.send().error("No search results for `$query`.").queue()
                return
            }
            val url: String = results[0].url

            context.send().text("**Video:** $url").queue()

            context.guildData.musicManager.youtubeResultsMap[context.member] = results to System.currentTimeMillis()
        } catch (e: JSONException) {
            context.send().error("Unable to get YouTube results.").queue()
            e.printStackTrace()
        } catch (e: NullPointerException) {
            context.send().error("Unable to get YouTube results.").queue()
            e.printStackTrace()
        }
    }
}



