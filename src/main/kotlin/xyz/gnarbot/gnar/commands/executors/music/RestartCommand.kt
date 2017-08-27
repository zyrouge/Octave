package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 71,
        aliases = arrayOf("restart"),
        description = "Restart the current song.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class RestartCommand : CommandExecutor() {
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

        if (context.voiceChannel != botChannel) {
            context.send().error("You're not in the same channel as the bot.").queue()
            return
        }

        val track = manager.player.playingTrack ?: manager.scheduler.lastTrack

        if (track != null) {
            context.send().embed("Restart Song") {
                desc { "Restarting track: `${track.info.embedTitle}`." }
            }.action().queue()

            manager.player.playTrack(track.makeClone())
        } else {
            context.send().error("No track has been previously started.").queue()
        }
    }
}
