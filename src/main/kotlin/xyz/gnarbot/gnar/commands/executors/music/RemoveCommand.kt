package xyz.gnarbot.gnar.commands.executors.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import java.util.*

@Command(
        id = 79,
        aliases = arrayOf("remove"),
        description = "Remove a song from the queue.",
        usage = "(first|last|#)",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class RemoveCommand : CommandExecutor() {
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

        if (context.member.voiceState.channel != botChannel) {
            context.send().error("You're not in the same channel as the bot.").queue()
            return
        }

        if (args.isEmpty()) {
            context.send().error("You should put something like: `_remove (first|last|#)`").queue()
            return
        }

        val queue = manager.scheduler.queue as LinkedList<AudioTrack>

        if (queue.isEmpty()) {
            context.send().error("The queue is empty.").queue()
            return
        }

        val track = when(args[0]) {
            "first" -> queue.removeFirst()
            "last" -> queue.removeLast()
            else -> {
                val num = args[0].toIntOrNull()

                if (num == null || num !in 1..queue.size) {
                    context.send().error("That is not a valid track number. Try `1..${queue.size}`, `first`, or `last`.").queue()
                    return
                }

                queue.removeAt(num - 1)
            }
        }

        context.send().embed("Remove Track") {
            desc {
                "Removed __[${track.info.title}](${track.info.uri})__ from the queue."
            }
        }.action().queue()
    }
}