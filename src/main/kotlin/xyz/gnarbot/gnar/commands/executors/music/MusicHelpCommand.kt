package xyz.gnarbot.gnar.commands.executors.general

import com.google.common.collect.Lists
import net.dv8tion.jda.core.entities.MessageEmbed
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.embed
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("music", "musichelp"),
        description = "Display Gnar's music commands in the channel.",
        category = Category.MUSIC
)
class MusicHelpCommand : CommandExecutor() {
    var lazyEmbed: MessageEmbed? = null

    override fun execute(context: Context, args: Array<String>) {
        val registry = Bot.getCommandRegistry()

        val cmds = registry.entries

        if (lazyEmbed == null) {
            lazyEmbed = embed("Music Commands") {
                description {
                    buildString {
                        append("Check out Gnar's music commands! 🌟 are donator commands, please consider ")
                        append("donating to our [Patreon](https://www.patreon.com/gnarbot) to gain access to them.").ln()
                    }
                }
                setColor(Bot.CONFIG.musicColor)

                val category = Category.MUSIC

                val filtered = cmds.filter {
                    it.info.category == category
                }

                val pages = Lists.partition(filtered, filtered.size / 3 + (if (filtered.size % 3 == 0) 0 else 1))

                for (page in pages) {
                    field("", true) {
                        buildString {
                            page.forEach {
                                append("[").append(Bot.CONFIG.prefix).append(it.info.aliases[0]).append("]()")

                                if (it.info.donor) {
                                    append(" 🌟").ln()
                                } else {
                                    ln()
                                }
                            }
                        }
                    }
                }
            }.build()
        }

        context.channel.sendMessage(lazyEmbed).queue()
    }
}