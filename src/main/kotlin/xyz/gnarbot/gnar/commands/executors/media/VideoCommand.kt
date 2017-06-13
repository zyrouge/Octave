package xyz.gnarbot.gnar.commands.executors.media

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context

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

        val query = args.joinToString(" ")

        MusicManager.search("ytsearch:$query", 1) { results ->
            if (results.isEmpty()) {
                context.send().error("No search results for `$query`.").queue()
                return@search
            }

            val url: String = results[0].info.uri

            context.send().text("**Video:** $url").queue()
        }
    }
}



