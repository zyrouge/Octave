package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.core.WebSocketCode
import net.dv8tion.jda.core.entities.impl.JDAImpl
import org.json.JSONObject
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln


@Command(
        id = 66,
        aliases = arrayOf("music", "musichelp"),
        description = "Display Gnar's music commands in the channel.",
        category = Category.MUSIC
)
class MusicHelpCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val registry = Bot.getCommandRegistry()

        if (args.isNotEmpty() && args[0] == "fix") {
            val obj = JSONObject()
                    .put("op", WebSocketCode.VOICE_STATE)
                    .put("d", JSONObject()
                            .put("guild_id", context.guild.id)
                            .put("channel_id", JSONObject.NULL)
                            .put("self_mute", false)
                            .put("self_deaf", false)
                    )
            (context.jda as JDAImpl).client.send(obj.toString())

            Bot.getPlayers().destroy(context.guild.idLong)

            context.send().text("This is a hacky fix, do not rely on it.").queue()
            return
        }

        val cmds = registry.entries
        context.send().embed("Music Commands") {
            desc {
                buildString {
                    append("Check out Gnar's music commands! Certain commands are donator exclusive, please consider ")
                    append("donating to our __**[Patreon](https://www.patreon.com/gnarbot)**__ to gain access to them.").ln()
                }
            }

            val category = Category.MUSIC

            val filtered = cmds.filter {
                it.info.category == category
            }

            field("Commands") {
                buildString {
                    filtered.forEach {
                        append('`')
                        append(it.info.aliases.first())
                        append("` • ")
                        append(it.info.description).ln()
                    }
                }
            }
        }.action().queue()
    }
}