package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.YouTube

@Command(
        aliases = arrayOf("play"),
        usage = "-(url|YT search)",
        description = "Joins and play music in a channel.",
        category = Category.MUSIC
)
class PlayCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = context.guildData.musicManager
        
        val botChannel = context.guild.selfMember.voiceState.channel
        val userChannel = context.guild.getMember(context.message.author).voiceState.channel

        if (botChannel != null && botChannel != userChannel) {
            context.send().error("The bot is already playing music in another channel.").queue()
            return
        }

        if (userChannel == null) {
            context.send().error("You must be in a voice channel to play music.").queue()
            return
        }

        if (args.isEmpty()) {
            if (manager.player.isPaused) {
                manager.player.isPaused = false
                context.send().embed("Play Music") {
                    setColor(context.bot.config.musicColor)
                    description { "Music is now playing." }
                }.action().queue()
            } else if (manager.player.playingTrack != null) {
                context.send().error("Music is already playing.").queue()
            } else if (manager.scheduler.queue.isEmpty()) {
                context.send().embed("Empty Queue") {
                    setColor(context.bot.config.musicColor)
                    description { "There is no music queued right now. Add some songs with `play -song|url`." }
                }.action().queue()
            }
            return
        }

        val url = if ("https://" in args[0] || "http://" in args[0]) {
            args[0]
        } else {
            val query = args.joinToString(" ").trim()

            if (query.startsWith("scsearch:", ignoreCase = true)
                    || query.startsWith("ytsearch:", ignoreCase = true)) {
                if (botChannel == null) {
                    context.guildData.musicManager.openAudioConnection(userChannel, context)
                }

                manager.loadAndPlay(context, query)
                return
            } else if (query.startsWith("youtube ", ignoreCase = true)) {
                if (botChannel == null) {
                    context.guildData.musicManager.openAudioConnection(userChannel, context)
                }

                manager.loadAndPlay(context, query.replaceFirst("youtube ", "ytsearch:", ignoreCase = true))
                return
            } else if (query.startsWith("soundcloud ", ignoreCase = true)) {
                if (botChannel == null) {
                    context.guildData.musicManager.openAudioConnection(userChannel, context)
                }

                manager.loadAndPlay(context, query.replaceFirst("soundcloud ", "scsearch:", ignoreCase = true))
                return
            }

            val results = YouTube.search(query, 1)

            if (results.isEmpty()) {
                context.send().error("No YouTube results returned for `${query.replace('+', ' ')}`.").queue()
                return
            }

            val result = results[0]
            result.url
        }

        if (botChannel == null) {
            context.guildData.musicManager.openAudioConnection(userChannel, context)
        }

        manager.loadAndPlay(context, url)
    }
}
