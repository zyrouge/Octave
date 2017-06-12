package xyz.gnarbot.gnar.commands.executors.donator

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.link

@Command(
        aliases = arrayOf("volume"),
        description = "Set the volume of the music player.",
        usage = "(loudness %)",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class VolumeCommand : CommandExecutor() {
    private val totalBlocks = 20

    override fun execute(context: Context, args: Array<String>) {
        val botChannel = context.guild.selfMember.voiceState.channel

        if (botChannel == null) {
            context.send().error("The bot is not currently playing anything.").queue()
            return
        }

        if (args.isEmpty()) {
            context.send().embed("Music Volume") {
                setColor(context.config.musicColor)

                field("", true) {
                    val percent = context.guildData.musicManager.player.volume.toDouble() / 100
                    buildString {
                        for (i in 0 until totalBlocks) {
                            if (i / totalBlocks.toDouble() > percent) {
                                append("\u25AC")
                            } else {
                                append("\u25AC" link "")
                            }
                        }
                        append(" **%.0f**%%".format(percent * 100))
                    }
                }

                setFooter("Set the volume by using _${info.aliases[0]} ${info.usage}.", null)
            }.action().queue()

            return
        }

        val amount = args[0].toDoubleOrNull()?.toInt()?.coerceIn(0, 100) ?: kotlin.run {
            context.send().error("Volume must be an integer.").queue()
            return
        }

        val old = context.guildData.musicManager.player.volume

        context.guildData.musicManager.player.volume = amount

        context.send().embed("Music Volume") {
            setColor(context.config.musicColor)

            description {
                val percent = amount.toDouble() / 100
                buildString {
                    for (i in 0 until totalBlocks) {
                        if (i / totalBlocks.toDouble() > percent) {
                            append("\u25AC")
                        } else {
                            append("\u25AC" link "")
                        }
                    }
                    append(" **%.0f**%%".format(percent * 100))
                }
            }

            setFooter("Volume changed from $old% to $amount%.", null)
        }.action().queue()
    }
}