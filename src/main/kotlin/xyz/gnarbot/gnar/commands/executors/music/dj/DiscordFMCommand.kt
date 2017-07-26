package xyz.gnarbot.gnar.commands.executors.music.dj

import org.jetbrains.annotations.NotNull
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.executors.music.PLAY_MESSAGE
import xyz.gnarbot.gnar.commands.executors.music.search.PlayCommand
import xyz.gnarbot.gnar.music.MusicLimitException
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.DiscordFMLibraries

@Command(
        id = 82,
        aliases = arrayOf("discordfm", "dfm"),
        usage = "(station name)",
        description = "Request songs from DiscordFM stations!",
        category = Category.MUSIC
)
class DiscordFMCommand : CommandExecutor() {

    override fun execute(context: Context, label: String, args: Array<String>) {
        if(args.isEmpty()) {
            context.send().error("No station was supplied. Available stations: ${DiscordFMLibraries.libraryTypes.contentToString()}").queue()
            return
        }

        for(s : String in DiscordFMLibraries.libraryTypes) {
            if(s.contains(args[0])) {
                Bot.getPlayers().get(context.guild)

                val manager = try {
                    Bot.getPlayers().get(context.guild)
                } catch (e: MusicLimitException) {
                    e.sendToContext(context)
                    return
                }

                manager.discordFMTrack = s
                manager.loadAndPlay(context, DiscordFMLibraries.getRandomSong(s), "Powered by Discord.FM")
            }
        }
    }
}