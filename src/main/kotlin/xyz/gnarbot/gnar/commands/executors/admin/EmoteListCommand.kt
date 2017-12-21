package xyz.gnarbot.gnar.commands.executors.admin

import com.google.common.collect.Lists
import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["emotes", "emotesList"],
        description = "Get all of the custom emotes the bot has access to"
)
@BotInfo(
        id = 34,
        category = Category.NONE,
        admin = true
)
class EmoteListCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        var page = args.firstOrNull()?.toIntOrNull() ?: 1

        context.send().embed("Emote List") {
            val totalEmotes = context.bot.shardManager.shardCache.flatMap { it.emoteCache }

            val pages = Lists.partition(totalEmotes, 30)

            if (page >= pages.size) page = pages.size
            else if (page <= 0) page = 1

            val emotePage = pages[page - 1]

            desc {
                buildString {
                    emotePage.forEach {
                        append("${it.asMention} `:${it.name}:`\n")
                    }
                }
            }

            setFooter("Page [$page/${pages.size}]", null)
        }.action().queue()
    }
}