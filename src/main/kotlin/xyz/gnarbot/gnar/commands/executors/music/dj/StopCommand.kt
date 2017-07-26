package xyz.gnarbot.gnar.commands.executors.music.dj

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.executors.music.PLAY_MESSAGE
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 61,
        aliases = arrayOf("stop", "leave", "reset"),
        description = "Stop and clear the music player.",
        category = Category.MUSIC,
        permissions = arrayOf(Permission.MANAGE_CHANNEL)
)
class StopCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val manager = Bot.getPlayers().getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n$PLAY_MESSAGE").queue()
            return
        }

        val botChannel = context.guild.selfMember.voiceState.channel
        if (botChannel == null) {
            context.send().error("The bot is not currently in a channel.\n$PLAY_MESSAGE").queue()
            return
        }

        manager.discordFMTrack = null
        Bot.getPlayers().destroy(context.guild.idLong)

        context.send().embed("Stop Playback") {
            desc { "Playback has been completely stopped and the queue has been cleared." }
        }.action().queue()
    }
}
