package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.RepeatOption
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 70,
        aliases = arrayOf("repeat"),
        usage = "(song, playlist, none)",
        description = "Set if the music player should repeat.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class RepeatCommand : CommandExecutor() {
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

        if (context.voiceChannel != botChannel) {
            context.send().error("You're not in the same channel as the bot.").queue()
            return
        }

        if (args.isEmpty()) {
            context.send().error("Please input one of these valid options `${RepeatOption.values().joinToString()}`").queue()
            return
        }

        val option = try {
            RepeatOption.valueOf(args[0].toUpperCase())
        } catch (e: IllegalArgumentException) {
            context.send().error("Valid options are `${RepeatOption.values().joinToString()}`").queue()
            return
        }

        manager.scheduler.repeatOption = option

        context.send().embed("Repeat Queue") {
            desc {
                when (manager.scheduler.repeatOption) {
                    RepeatOption.QUEUE -> "\uD83D\uDD01"
                    RepeatOption.SONG -> "\uD83D\uDD02"
                    RepeatOption.NONE -> "\u274C"
                } + " Music player was set to __**${manager.scheduler.repeatOption}**__."
            }
        }.action().queue()
    }
}
