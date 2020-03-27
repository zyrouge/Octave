package xyz.gnarbot.gnar.commands.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager
import java.util.*
import java.util.regex.Pattern

@Command(
        aliases = ["remove", "removesong"],
        description = "Remove a song from the queue.",
        usage = "(first|last|all|start..end|#)"
)
@BotInfo(
        id = 79,
        category = Category.MUSIC,
        scope = Scope.VOICE,
        djLock = true
)
class RemoveCommand : MusicCommandExecutor(true, false, false) {
    private val pattern = Pattern.compile("(\\d+)?\\s*?\\.\\.\\s*(\\d+)?")

    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val queue = manager.scheduler.queue as LinkedList<AudioTrack>

        if (queue.isEmpty()) {
            context.send().error("The queue is empty.").queue()
            return
        }

        val track = when (args.firstOrNull()) {
            null -> return context.send().issue("You need to specify what to remove.").queue()
            "first" -> queue.removeFirst()
            "last" -> queue.removeLast()
            "all" -> {
                queue.clear()
                context.send().info("Cleared the music queue.").queue()
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
                            context.send().error("Invalid start of range.").queue()
                            return
                        }
                    }

                    val end = matcher.group(2).let {
                        if (it == null) queue.size
                        else try {
                            it.toInt().coerceAtMost(queue.size)
                        } catch (e: NumberFormatException) {
                            context.send().error("Invalid end of range.").queue()
                            return
                        }
                    }

                    for (i in start..end) {
                        queue.removeAt(i - 1)
                    }

                    context.send().info("Removed track number `$start..$end` from the queue.").queue()

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

        context.send().info("Removed __[${track.info.embedTitle}](${track.info.embedUri})__ from the queue.").queue()
    }
}