package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.MessageEmbed
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.u

@Command(
        aliases = arrayOf("queue", "list"),
        usage = "[clear]",
        description = "Shows the music that's currently queued.",
        category = Category.MUSIC
)
class QueueCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val queue = context.guildData.musicManager.scheduler.queue

        var trackCount = 0
        var queueLength = 0L

        if (args.isNotEmpty()) {
            if (args[0] == "clear") {
                if (!context.member.hasPermission(context.guild.selfMember.voiceState.channel, Permission.MANAGE_CHANNEL)) {
                    context.send().error("You lack the following permissions: `Manage Channels` in the the voice channel `${context.guild.selfMember.voiceState.channel.name}`.").queue()
                    return
                }
                queue.clear()
                context.send().embed("Music Queue") {
                    setColor(context.bot.config.musicColor)
                    setDescription("Cleared the music queue.")
                }.action().queue()
                return
            }
        }

        context.send().embed("Music Queue") {
            setColor(context.bot.config.musicColor)

            context.guildData.musicManager.player.playingTrack?.let {
                field("Now Playing", false) {
                    "__[${it.info.title}](${it.info.uri})__"
                }
            }

            field("Queue", false) {
                buildString {
                    if (queue.isEmpty()) {
                        append(u("Empty queue.") + " Add some music with `_play url|YT search`.")
                    } else for (track in queue) {
                        queueLength += track.duration
                        trackCount++

                        appendln("**$trackCount** `[${Utils.getTimestamp(track.duration)}]` __[${track.info.title}](${track.info.uri})__")

                        if (length >= MessageEmbed.VALUE_MAX_LENGTH - 200) {
                            append("... and **${queue.size - trackCount}** more tracks.")
                            break
                        }
                    }
                }
            }

            field("Entries", true) { trackCount }
            field("Queue Duration", true) { Utils.getTimestamp(queueLength) }
        }.action().queue()
    }
}
