package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 74,
        aliases = arrayOf("volume"),
        description = "Set the volume of the music player.",
        usage = "(loudness %)",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class VolumeCommand : CommandExecutor() {
    private val totalBlocks = 20

    override fun execute(context: Context, label: String, args: Array<String>) {
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

        if (args.isEmpty()) {
            context.send().embed("Music Volume") {
                desc {
                    val percent = manager.player.volume.toDouble() / 100
                    buildString {
                        append("[")
                        for (i in 0..totalBlocks - 1) {
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

        val amount = args[0].toDoubleOrNull()?.toInt()?.coerceIn(0, 150)
                ?: return context.send().error("Volume must be an integer.").queue()

        val old = manager.player.volume

        manager.player.volume = amount

        context.guildOptions.musicVolume = amount
        context.guildOptions.save()

        context.send().embed("Music Volume") {
            desc {
                val percent = amount.toDouble() / 100
                buildString {
                    append("[")
                    for (i in 0..totalBlocks - 1) {
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

            if (old == amount) {
                setFooter("Volume remained the same.", null)
            } else {
                setFooter("Volume changed from $old% to $amount%.", null)
            }
        }.action().queue()
    }
}