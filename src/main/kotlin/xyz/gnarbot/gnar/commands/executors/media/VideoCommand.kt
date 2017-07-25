package xyz.gnarbot.gnar.commands.executors.media

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 51,
        aliases = arrayOf("video", "vid"),
        usage = "(query...)",
        description = "Search and get a YouTube video.",
        scope = Scope.TEXT,
        category = Category.MEDIA
)
class VideoCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            CommandDispatcher.sendHelp(context, info)
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



