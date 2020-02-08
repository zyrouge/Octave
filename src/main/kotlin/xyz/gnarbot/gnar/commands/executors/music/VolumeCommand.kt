package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager

@Command(
        aliases = ["volume"],
        description = "Set the volume of the music player.",
        usage = "(loudness %)"
)
@BotInfo(
        id = 74,
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class VolumeCommand : MusicCommandExecutor(false, false) {
    private val totalBlocks = 20

    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        if (args.isEmpty()) {
            context.send().embed("Music Volume") {
                desc {
                    val percent = (manager.player.volume.toDouble() / 100.0).coerceIn(0.0, 1.0)
                    buildString {
                        append("[")
                        for (i in 0 until totalBlocks) {
                            if ((percent * (totalBlocks - 1)).toInt() == i) {
                                append("\u25AC")
                                append("]()")
                            } else {
                                append("\u25AC")
                            }
                        }
                        append(" **%.0f**%%".format(percent * 100))
                    }
                }

                setFooter("Set the volume by using _${info.aliases[0]} ${info.usage}.", null)
            }.action().queue()

            return
        }

        val amount = try {
            args[0].toInt().coerceIn(0, 150)
        } catch (e: NumberFormatException) {
            context.send().error("Volume must be an integer.").queue()
            return
        }

        val old = manager.player.volume

        manager.player.volume = amount

        context.data.music.volume = amount
        context.data.save()

        context.send().embed("Music Volume") {
            desc {
                val percent = (amount.toDouble() / 100).coerceIn(0.0, 1.0)
                buildString {
                    append("[")
                    for (i in 0 until totalBlocks) {
                        if ((percent * (totalBlocks - 1)).toInt() == i) {
                            append("\u25AC")
                            append("]()")
                        } else {
                            append("\u25AC")
                        }
                    }
                    append(" **%.0f**%%".format(percent * 100))
                }
            }

            footer {
                if (old == amount) {
                    "Volume remained the same."
                } else {
                    "Volume changed from $old% to $amount%."
                }
            }
        }.action().queue()
    }
}