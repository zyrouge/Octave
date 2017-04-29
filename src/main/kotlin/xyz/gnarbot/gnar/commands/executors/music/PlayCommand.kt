package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.BotConfiguration
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
                    color = BotConfiguration.MUSIC_COLOR
                    description = "Music is now playing."
                }.action().queue()
            } else if (manager.player.playingTrack != null) {
                context.send().error("Music is already playing.").queue()
            } else if (manager.scheduler.queue.isEmpty()) {
                context.send().embed("Empty Queue") {
                    color = BotConfiguration.MUSIC_COLOR
                    description = "There is no music queued right now. Add some songs with `play -song|url`."
                }.action().queue()
            }
            return
        }

        val url = if ("https://" in args[0] || "http://" in args[0]) {
            args[0]
        } else {
            val query = args.joinToString("+")

            val results = YouTube.search(query, 1)

            if (results.isEmpty()) {
                context.send().error("No YouTube results returned for `${query.replace('+', ' ')}`.").queue()
                return
            }

            val result = results[0]
            result.url
        }

        if (botChannel == null) {
            context.guild.audioManager.sendingHandler = manager.sendHandler
            context.guild.audioManager.openAudioConnection(userChannel)

            context.send().embed("Music Playback") {
                color = BotConfiguration.MUSIC_COLOR
                description = "Joined channel `${userChannel.name}`."
            }.action().queue()
        }

        manager.loadAndPlay(context, url)
    }
}
