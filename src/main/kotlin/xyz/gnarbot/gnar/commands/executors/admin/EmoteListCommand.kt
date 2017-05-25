package xyz.gnarbot.gnar.commands.executors.general

import com.google.common.collect.Lists
import net.dv8tion.jda.core.entities.Emote
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("emoteList"),
        description = "Get shard information.",
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

        context.send().embed("Shard Information") {
            color = context.bot.config.accentColor

            val totalEmotes = mutableListOf<Emote>();

            context.bot.shards.forEach{
                it.emotes.forEach {
                    totalEmotes.add(it);
                }
            }

            val pages = Lists.partition(totalEmotes, 30)

            if (page >= pages.size) page = pages.size
            else if (page <= 0) page = 1

            val emotePage = pages[page - 1]

            emotePage.forEach {
                field("Emotes", true) {
                    buildString {
                        append("${it.name}: ${it.asMention}")
                    }
                }
            }

            footer = "Page [$page/${pages.size}]"
        }.action().queue()
    }
}