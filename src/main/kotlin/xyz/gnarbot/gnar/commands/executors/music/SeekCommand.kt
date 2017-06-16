package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils

@Command(
        aliases = arrayOf("seek"),
        description = "Skip the current music track.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class SeekCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = context.guildData.musicManager

        val botChannel = context.guild.selfMember.voiceState.channel
        if (botChannel == null) {
            context.send().error("The bot is not currently in a channel.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to start playing some music!").queue()
            return
        }

        if (!(context.member.hasPermission(Permission.MANAGE_CHANNEL)
                || manager.player.playingTrack.userData == context.member)) {
            context.send().error("You did not request this track.").queue()
            return
        }

        if (args.isEmpty()) {
            context.send().error("Please input a timestamp, ex: `1 hour 2 seconds` `1m30s` `2 minutes`.").queue()
            return
        }

        if (manager.player.playingTrack == null) {
            context.send().error("This track is not seekable.").queue()
            return
        }

        if (!manager.player.playingTrack.isSeekable) {
            context.send().error("This track is not seekable.").queue()
            return
        }

        val relative = args[0] == "+" || args[0] == "-"

        val query = args.joinToString(" ")

        val ms = Utils.parseTimestamp(query)

        if (ms < 0) {
            context.send().error("Time value can not be negative.").queue()
            return
        }

        manager.player.playingTrack.position = ms

        context.send().embed("Seek Length") {
            setColor(Bot.CONFIG.musicColor)
            setDescription("The position of the track has been set to ${Utils.getTimestamp(ms)}.")
        }.action().queue()
    }
}
