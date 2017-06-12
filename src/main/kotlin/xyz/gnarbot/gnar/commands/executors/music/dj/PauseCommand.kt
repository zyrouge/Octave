package xyz.gnarbot.gnar.commands.executors.music.dj

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("pause"),
        description = "Pause or resume the music player.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        permissions = arrayOf(Permission.MANAGE_CHANNEL)
)
class PauseCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = context.guildData.musicManager

        if (manager.player.playingTrack == null) {
            context.send().error("Can not pause or resume player because there is no track loaded for playing.").queue()
            return
        }

        manager.player.isPaused = !manager.player.isPaused

        context.send().embed("Playback Control") {
            setColor(context.bot.config.musicColor)
            description {
                if (manager.player.isPaused) {
                    "The player has been paused."
                } else {
                    "The player has resumed playing."
                }
            }
        }.action().queue()
    }
}
