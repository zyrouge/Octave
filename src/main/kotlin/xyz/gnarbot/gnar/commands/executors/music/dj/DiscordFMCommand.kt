package xyz.gnarbot.gnar.commands.executors.music.dj

import org.jetbrains.annotations.NotNull
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.executors.music.PLAY_MESSAGE
import xyz.gnarbot.gnar.commands.executors.music.search.PlayCommand
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.DiscordFMLibraries

@Command(
        id = 79,
        aliases = arrayOf("discordfm", "dfm"),
        usage = "(station name)",
        description = "Request songs from DiscordFM stations!",
        category = Category.MUSIC
)
class DiscordFMCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val manager = Bot.getPlayers().getExisting(context.guild)

        if(manager == null) {
            context.send().error(PLAY_MESSAGE).queue()
            return
        }

        if(args.isEmpty()) {
            context.send().error("No station was supplied. Available stations: ${DiscordFMLibraries.libraryTypes.contentToString()}").queue()
            return
        }

        for(s : String in DiscordFMLibraries.libraryTypes) {
            if(s.contains(args[0])) {
                manager.discordFMTrack = args[0]

                manager.loadAndPlay(context, DiscordFMLibraries.getRandomSong(args[0]), "")
            }
        }
    }
}