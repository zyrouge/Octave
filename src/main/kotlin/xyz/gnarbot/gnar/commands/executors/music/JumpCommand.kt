package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.commands.managed.Executor
import xyz.gnarbot.gnar.commands.managed.ManagedCommand
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import java.time.Duration

@Command(
        aliases = arrayOf("jump", "seek"),
        usage = "(to|forward|backward) (time)",
        description = "Set the time marker of the music playback.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class JumpCommand : ManagedCommand() {
    @Executor(0, description = "Set the time marker of the player.")
    fun to(context: Context, duration: Duration) {
        if (!check(context)) return

        val manager = Bot.getPlayers().getExisting(context.guild)!!

        manager.player.playingTrack.position = duration.toMillis().coerceIn(0, manager.player.playingTrack.duration)

        context.send().embed("Jump To") {
            desc { "The position of the track has been set to ${Utils.getTimestamp(manager.player.playingTrack.position)}." }
        }.action().queue()
    }

    @Executor(1, description = "Move the time marker forward.")
    fun forward(context: Context, duration: Duration) {
        if (!check(context)) return

        val manager = Bot.getPlayers().getExisting(context.guild)!!

        manager.player.playingTrack.position = (manager.player.playingTrack.position + duration.toMillis())
                .coerceIn(0, manager.player.playingTrack.duration)

        context.send().embed("Jump Forward") {
            desc { "The position of the track has been set to ${Utils.getTimestamp(manager.player.playingTrack.position)}." }
        }.action().queue()
    }

    @Executor(2, description = "Move the time marker backward.")
    fun backward(context: Context, duration: Duration) {
        if (!check(context)) return

        val manager = Bot.getPlayers().getExisting(context.guild)!!

        manager.player.playingTrack.position = (manager.player.playingTrack.position - duration.toMillis())
                .coerceIn(0, manager.player.playingTrack.duration)

        context.send().embed("Jump Backward") {
            desc { "The position of the track has been set to ${Utils.getTimestamp(manager.player.playingTrack.position)}." }
        }.action().queue()
    }

    fun check(context: Context): Boolean {
        val manager = Bot.getPlayers().getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n" +
                    "\uD83C\uDFB6` _play (song/url)` in a voice channel to start playing some music!").queue()
            return false
        }

        val botChannel = context.guild.selfMember.voiceState.channel
        if (botChannel == null) {
            context.send().error("The bot is not currently in a channel.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to start playing some music!").queue()
            return false
        }

        if (manager.player.playingTrack == null) {
            context.send().error("You are not playing any music.").queue()
            return false
        }

        if (!manager.player.playingTrack.isSeekable) {
            context.send().error("This track is not seekable.").queue()
            return false
        }
        return true
    }
}
