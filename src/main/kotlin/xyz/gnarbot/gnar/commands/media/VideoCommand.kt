package xyz.gnarbot.gnar.commands.media

import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["video", "vid"],
        usage = "(query...)",
        description = "Search and get a YouTube video."
)
@BotInfo(
        id = 51,
        scope = Scope.TEXT,
        category = Category.MEDIA
)
class VideoCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            context.bot.commandDispatcher.sendHelp(context, info)
            return
        }

        val query = args.joinToString(" ")

        context.bot.players.get(context.guild).search("ytsearch:$query", 1) { results ->
            if (results.isEmpty()) {
                context.send().error("No search results for `$query`.").queue()
                return@search
            }

            val url: String = results[0].info.uri

            context.send().text("**Video:** $url").queue()
        }
    }
}



