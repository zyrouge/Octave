package xyz.gnarbot.gnar.commands.executors.music.search

import org.apache.commons.lang3.StringUtils
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
            context.send().embed("Discord FM") {
                desc {
                    buildString {
                        append("Stream random songs from DiscordFM stations!\n")
                        append("Select and stream a station using `_dfm (station name)`.\n")
                        append("Stop streaming songs from a station with `_dfm stop`,")
                    }
                }

                field("Available Stations") {
                    buildString {
                        DiscordFM.LIBRARIES.forEach {
                            append("• `").append(it.capitalize()).append("`\n")
                        }
                    }
                }
            }.action().queue()
            return
        }

        if (args[0] == "stop") {
            val manager = Bot.getPlayers().getExisting(context.guild)

            if (manager == null) {
                context.send().error("There's no music player in this guild.\n$PLAY_MESSAGE").queue()
                return
            }

            if (manager.discordFMTrack == null) {
                context.send().error("I'm not streaming random songs from a Discord.FM station.").queue()
                return
            }

            val station = manager.discordFMTrack!!.station.capitalize()
            manager.discordFMTrack = null

            context.send().embed("Discord.FM") {
                desc { "No longer streaming random songs from the `$station` station." }
            }.action().queue()
        }

        val query = args.joinToString(" ").toLowerCase()

        // quick check for incomplete query
        // classic -> classical
        var library = DiscordFM.LIBRARIES.firstOrNull { it.contains(query) }

        if (library == null) {
            var maxDistance = 10
            DiscordFM.LIBRARIES.forEach {
                val distance = StringUtils.getLevenshteinDistance(it, query)
                if (distance < maxDistance) {
                    maxDistance = distance
                    library = it
                }
            }
        }

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

        DiscordFMTrackContext(library!!, context.user.idLong, context.channel.idLong).let {
            manager.discordFMTrack = it
            manager.loadAndPlay(context,
                    DiscordFM.getRandomSong(library),
                    it,
                    "Now streaming random tracks from the `$library` Discord.FM station!"
            )
        }
    }
}