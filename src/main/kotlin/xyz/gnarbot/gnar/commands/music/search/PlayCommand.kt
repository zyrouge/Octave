package xyz.gnarbot.gnar.commands.music.search

import org.jetbrains.kotlin.backend.common.onlyIf
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicLimitException
import xyz.gnarbot.gnar.music.TrackContext

@Command(
        aliases = ["play", "p"],
        usage = "[url|YT search]",
        description = "Plays music in a voice channel"
)
@BotInfo(
        id = 62,
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class PlayCommand : CommandExecutor() {
    private val footnote = "You can search and pick results using ${config.prefix}youtube or ${config.prefix}soundcloud while in a channel."

    override fun execute(context: Context, label: String, args: Array<String>) {
        val botChannel = context.selfMember.voiceState?.channel
        val userChannel = context.voiceChannel

        if (botChannel != null && botChannel != userChannel) {
            context.send().issue("The bot is already playing music in another channel.").queue()
            return
        }

        if (args.isEmpty()) {
            val manager = context.bot.players.getExisting(context.guild)
            if (manager == null) {
                context.send().issue("There's no music player in this guild.\n" +
                        "\uD83C\uDFB6` ${config.prefix}play (song/url)` to start playing some music!").queue()
                return
            }

            when {
                manager.player.isPaused -> {
                    manager.player.isPaused = false
                    context.send().embed("Play Music") {
                        desc { "Music is no longer paused." }
                    }.action().queue()
                }
                manager.player.playingTrack != null -> {
                    context.send().error("Music is already playing. Are you trying to queue a track? Try adding a search term with this command!").queue()
                }
                manager.scheduler.queue.isEmpty() -> {
                    context.send().embed("Empty Queue") {
                        desc { "There is no music queued right now. Add some songs with `${config.prefix}play -song|url`." }
                    }.action().queue()
                }
            }
            return
        }

        if ("https://" in args[0] || "http://" in args[0]) {
            val link = args[0].removePrefix("<").removeSuffix(">")

            val manager = try {
                context.bot.players.get(context.guild)
            } catch (e: MusicLimitException) {
                e.sendToContext(context)
                return
            }

            manager.loadAndPlay(
                    context,
                    link,
                    TrackContext(
                            context.member.user.idLong,
                            context.textChannel.idLong
                    ),
                    footnote
            )
        } else {
            if (!context.bot.configuration.searchEnabled) {
                context.send().issue("Search is currently disabled. Try direct links instead.").queue()
                return
            }

            val query = args.joinToString(" ").trim()

//            context.bot.players.get(context.guild).search("ytsearch:$query", 1) { results ->
//                if (results.isEmpty()) {
//                    context.send().issue("No YouTube results returned for `${query.replace('+', ' ')}`.").queue()
//                    return@search
//                }
//
//                val result = results[0]

                val manager = try {
                    context.bot.players.get(context.guild)
                } catch (e: MusicLimitException) {
                    e.sendToContext(context)
                    return
                }

                manager.loadAndPlay(
                        context,
                        "ytsearch:$query",
                        TrackContext(
                                context.member.user.idLong,
                                context.textChannel.idLong
                        ),
                        footnote
                )
//            }
        }
    }
}
