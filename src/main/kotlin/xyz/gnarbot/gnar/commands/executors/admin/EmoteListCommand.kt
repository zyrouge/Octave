package xyz.gnarbot.gnar.commands.executors.general

import com.google.common.collect.Lists
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("emotes", "emotesList"),
        description = "Get all of the custom emotes the bot has access to",
        category = Category.NONE,
        administrator = true
)
class EmoteListCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        var page = if (args.isNotEmpty()) {
            args[0].toIntOrNull() ?: 1
        } else {
            1
        }

        context.send().embed("Emote List") {
            val totalEmotes = context.bot.shards.flatMap { it.emotes }

            val pages = Lists.partition(totalEmotes, 30)

            if (page >= pages.size) page = pages.size
            else if (page <= 0) page = 1

            val emotePage = pages[page - 1]

            description {
                buildString {
                    emotePage.forEach {
                        append("${it.asMention} `:${it.name}:`")
                    }
                }
            }

            footer = "Page [$page/${pages.size}]"
        }.action().queue()
    }
}