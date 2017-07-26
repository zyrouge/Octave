package xyz.gnarbot.gnar.commands.executors.music.search

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.commands.executors.music.PLAY_MESSAGE
import xyz.gnarbot.gnar.music.DiscordFMTrackContext
import xyz.gnarbot.gnar.music.MusicLimitException
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.DiscordFM

@Command(
        id = 82,
        aliases = arrayOf("discordfm", "dfm"),
        usage = "(station name)|stop",
        description = "Stream random songs from DiscordFM stations!",
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class DiscordFMCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("No station was supplied. Available stations: `${DiscordFM.LIBRARIES.contentToString()}`.").queue()
            return
        }

        if (args[0] == "stop") {
            val manager = Bot.getPlayers().getExisting(context.guild)

            if (manager == null) {
                context.send().error("There's no music player in this guild.\n$PLAY_MESSAGE").queue()
                return
            }

            if (manager.discordFMTrack == null) {
                context.send().error("I'm not streaming random songs from a Discord.FM station.")
            } else {
                val station = manager.discordFMTrack!!.station.capitalize()
                manager.discordFMTrack = null

                context.send().embed("Discord.FM") {
                    desc { "No longer streaming random songs from the `$station` station." }
                }.action().queue()
            }
            return
        }

        val query = args.joinToString(" ").toLowerCase()

        val library = DiscordFM.LIBRARIES.firstOrNull { it.contains(query) }

        if (library == null) {
            context.send().error("Library $query doesn't exist. Available stations: `${DiscordFM.LIBRARIES.contentToString()}`.").queue()
            return
        }

        val manager = try {
            Bot.getPlayers().get(context.guild)
        } catch (e: MusicLimitException) {
            e.sendToContext(context)
            return
        }

        DiscordFMTrackContext(query, context.user.idLong, context.channel.idLong).let {
            manager.discordFMTrack = it
            manager.loadAndPlay(context,
                    DiscordFM.getRandomSong(library),
                    it,
                    "Now streaming random tracks from the ${library.capitalize()} Discord.FM station!"
            )
        }
    }
}