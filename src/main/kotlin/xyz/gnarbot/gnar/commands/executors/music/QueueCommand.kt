package xyz.gnarbot.gnar.commands.executors.music

import com.jagrosh.jdautilities.menu.PaginatorBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Member
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils

@Command(
        aliases = arrayOf("queue", "list"),
        usage = "[clear]",
        description = "Shows the music that's currently queued.",
        category = Category.MUSIC
)
class QueueCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val queue = context.guildData.musicManager.scheduler.queue

        if (args.isNotEmpty()) {
            if (args[0] == "clear") {
                if (!context.member.hasPermission(context.guild.selfMember.voiceState.channel, Permission.MANAGE_CHANNEL)) {
                    context.send().error("You lack the following permissions: `Manage Channels` in the the voice channel `${context.guild.selfMember.voiceState.channel.name}`.").queue()
                    return
                }
                queue.clear()
                context.send().embed("Music Queue") {
                    setColor(Bot.CONFIG.musicColor)
                    setDescription("Cleared the music queue.")
                }.action().queue()
                return
            }
        }

        PaginatorBuilder(Bot.getWaiter())
                .setTitle("Music Queue")
                .setColor(Bot.CONFIG.musicColor)
                .apply {
                    if (queue.isEmpty()) {
                        add("**Empty queue.** Add some music with `_play url|YT search`.")
                    } else for (track in queue) {
                        add("`[${Utils.getTimestamp(track.duration)}]` __[${track.info.title}](${track.info.uri})__ from ${track.getUserData(Member::class.java).asMention}")
                    }
                }
                .build().display(context.channel)
    }
}
