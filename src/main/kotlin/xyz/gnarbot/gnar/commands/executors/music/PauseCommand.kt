package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 68,
        aliases = arrayOf("pause"),
        description = "Pause or resume the music player.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class PauseCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
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

        if (manager.player.playingTrack == null) {
            context.send().error("Can not pause or resume player because there is no track loaded for playing.").queue()
            return
        }

        manager.player.isPaused = !manager.player.isPaused

        context.send().embed("Playback Control") {
            desc {
                if (manager.player.isPaused) {
                    "The player has been paused."
                } else {
                    "The player has resumed playing."
                }
            }
        }.action().queue()
    }
}
