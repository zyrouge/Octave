package xyz.gnarbot.gnar.commands.executors.music.dj

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("restart"),
        description = "Restart the current song.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        permissions = arrayOf(Permission.MANAGE_CHANNEL)
)
class RestartCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = Bot.getPlayers().getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to start playing some music!").queue()
            return
        }

        val botChannel = context.guild.selfMember.voiceState.channel
        if (botChannel == null) {
            context.send().error("The bot is not currently in a channel.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to start playing some music!").queue()
            return
        }

        val track = manager.player.playingTrack ?: manager.scheduler.lastTrack

        if (track != null) {
            context.send().embed("Restart Song") {
                setColor(Bot.CONFIG.musicColor)
                setDescription("Restarting track: `${track.info.title}`.")
            }.action().queue()

            manager.player.playTrack(track.makeClone())
        } else {
            context.send().error("No track has been previously started.").queue()
        }
    }
}
