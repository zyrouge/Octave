package xyz.gnarbot.gnar.commands.executors.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context
import java.util.*
import java.util.regex.Pattern

@Command(
        id = 79,
        aliases = arrayOf("remove"),
        description = "Remove a song from the queue.",
        usage = "(first|last|all|#)",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class RemoveCommand : MusicCommandExecutor(true, false) {
    private val pattern = Pattern.compile("(\\d+)?\\s*?\\.\\.\\s*(\\d+)?")

    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val queue = manager.scheduler.queue as LinkedList<AudioTrack>

        if (queue.isEmpty()) {
            context.send().error("The queue is empty.").queue()
            return
        }

        val track = when(args[0]) {
            "first" -> queue.removeFirst()
            "last" -> queue.removeLast()
            "all" -> {
                queue.clear()

                context.send().embed("Queue Clear") {
                    desc { "Cleared the music queue." }
                }.action().queue()

                return
            }
            else -> {
                val arg = args.joinToString(" ")

                val matcher = pattern.matcher(arg)
                if (matcher.find()) {
                    val start = matcher.group(1).let {
                        if (it == null) 1
                        else try {
                            it.toInt().coerceAtLeast(1)
                        } catch (e: NumberFormatException) {
                            context.send().error("Invalid start of range.")
                            return
                        }
                    }

                    val end = matcher.group(2).let {
                        if (it == null) queue.size
                        else try {
                            it.toInt().coerceAtMost(queue.size)
                        } catch (e: NumberFormatException) {
                            context.send().error("Invalid end of range.")
                            return
                        }
                    }

                    for (i in start..end) {
                        queue.removeAt(i - 1)
                    }

                    context.send().embed("Remove Track") {
                        desc { "Removed track number `$start..$end` from the queue." }
                    }.action().queue()

                    return
                }

                val num = arg.toIntOrNull()

                if (num == null || num !in 1..queue.size) {
                    context.send().error("That is not a valid track number. Try `1`, `1..${queue.size}`, `first`, or `last`.").queue()
                    return
                }

                queue.removeAt(num - 1)
            }
        }

        context.send().embed("Remove Track") {
            desc { "Removed __[${track.info.embedTitle}](${track.info.uri})__ from the queue." }
        }.action().queue()
    }
}