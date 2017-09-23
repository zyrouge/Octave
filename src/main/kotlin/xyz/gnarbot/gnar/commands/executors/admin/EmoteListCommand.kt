package xyz.gnarbot.gnar.commands.executors.admin

import com.google.common.collect.Lists
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 34,
        aliases = arrayOf("emotes", "emotesList"),
        description = "Get all of the custom emotes the bot has access to",
        category = Category.NONE,
        admin = true
)
class EmoteListCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        var page = args.firstOrNull()?.toIntOrNull() ?: 1

        context.send().embed("Emote List") {
            val totalEmotes = Bot.getShards().flatMap { it.jda.emoteCache }

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