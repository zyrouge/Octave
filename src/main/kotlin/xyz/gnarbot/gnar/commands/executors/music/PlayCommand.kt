package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.core.entities.Message
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.YouTube

@Command(
        aliases = arrayOf("play"),
        usage = "-(url|YT search)",
        description = "Joins and play music in a channel.",
        category = Category.MUSIC
)
class PlayCommand : CommandExecutor() {
    override fun execute(message: Message, args: Array<String>) {
        val manager = guildData.musicManager
        
        val botChannel = guild.selfMember.voiceState.channel
        val userChannel = guild.getMember(message.author).voiceState.channel

        if (botChannel != null && botChannel != userChannel) {
            message.send().error("The getBot is already playing music in another channel.").queue()
            return
        }

        if (userChannel == null) {
            message.send().error("You must be in a voice channel to play music.").queue()
            return
        }

        if (args.isEmpty()) {
            if (manager.player.isPaused) {
                manager.player.isPaused = false
                message.send().embed("Play Music") {
                    color = BotConfiguration.MUSIC_COLOR
                    description = "Music is now playing."
                }.rest().queue()
            } else if (manager.player.playingTrack != null) {
                message.send().error("Music is already playing.").queue()
            } else if (manager.scheduler.queue.isEmpty()) {
                message.send().embed("Empty Queue") {
                    color = BotConfiguration.MUSIC_COLOR
                    description = "There is no music queued right now. Add some songs with `play -song|url`."
                }.rest().queue()
            }
            return
        }

        val url = if ("https://" in args[0] || "http://" in args[0]) {
            args[0]
        } else {
            val query = args.joinToString("+")

            val results = YouTube.search(query, 1)

            if (results.isEmpty()) {
                message.send().error("No YouTube results returned for `${query.replace('+', ' ')}`.").queue()
                return
            }

            val result = results[0]
            result.url
        }

        if (botChannel == null) {
            guild.audioManager.sendingHandler = manager.sendHandler
            guild.audioManager.openAudioConnection(userChannel)

            message.send().embed("Music Playback") {
                color = BotConfiguration.MUSIC_COLOR
                description = "Joined channel `${userChannel.name}`."
            }.rest().queue()
        }

        manager.loadAndPlay(message, url)
    }
}
