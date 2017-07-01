package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioTrack
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.utils.Context

class MusicManager(val id: Long, val jda: JDA) {
    companion object {
        val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
                .apply(AudioSourceManagers::registerRemoteSources)

        fun search(query: String, maxResults: Int, callback: (List<AudioTrack>) -> Unit) {
            playerManager.loadItem(query, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    callback(listOf(track))
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    if (!playlist.isSearchResult) {
                        return
                    }

                    callback(playlist.tracks.subList(0, Math.min(maxResults, playlist.tracks.size)))
                }

                override fun noMatches() {
                    callback(emptyList())
                }

                override fun loadFailed(e: FriendlyException) {
                    callback(emptyList())
                }
            })
        }
    }

    val guild: Guild get() = jda.getGuildById(id)


    /** @return Audio player for the guild. */
    val player: AudioPlayer = playerManager.createPlayer()

    /**  @return Track scheduler for the player.*/
    val scheduler: TrackScheduler = TrackScheduler(this, player).also(player::addListener)

    /** @return Wrapper around AudioPlayer to use it as an AudioSendHandler. */
    val sendHandler: AudioPlayerSendHandler = AudioPlayerSendHandler(player)

    /**
     * @return Voting cooldown.
     */
    var lastVoteTime: Long = 0L

    /**
     * @return Whether there is a vote to skip the song or not.
     */
    var isVotingToSkip = false

    fun destroy() {
        player.destroy()
        closeAudioConnection()
    }

    fun openAudioConnection(channel: VoiceChannel, context: Context) : Boolean {
        when {
            !Bot.CONFIG.musicEnabled -> {
                context.send().error("Music is disabled.").queue()
                Bot.getPlayers().destroy(id)
                return false
            }
            !guild.selfMember.hasPermission(channel, Permission.VOICE_CONNECT) -> {
                context.send().error("The bot can't connect to this channel due to a lack of permission.").queue()
                Bot.getPlayers().destroy(id)
                return false
            }
            else -> {
                guild.audioManager.sendingHandler = sendHandler
                guild.audioManager.openAudioConnection(channel)

                context.send().embed("Music Playback") {
                    setColor(Bot.CONFIG.musicColor)
                    setDescription("Joining channel `${channel.name}`.")
                }.action().queue()
                return true
            }
        }
    }

    fun closeAudioConnection() {
        guild.audioManager.closeAudioConnection()
        guild.audioManager.sendingHandler = null
    }

    fun loadAndPlay(context: Context, trackUrl: String, footnote: String? = null) {
        playerManager.loadItemOrdered(this, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                if (!guild.selfMember.voiceState.inVoiceChannel()) {
                    if (!context.member.voiceState.inVoiceChannel()) {
                        context.send().error("You left the channel before the track is loaded.").queue()

                        // Track is not supposed to load and the queue is empty
                        // destroy player
                        if (scheduler.queue.isEmpty()) {
                            Bot.getPlayers().destroy(id)
                        }
                        return
                    }
                    openAudioConnection(context.member.voiceState.channel, context)
                }

                if (scheduler.queue.size >= Bot.CONFIG.queueLimit) {
                    context.send().error("The queue can not exceed ${Bot.CONFIG.queueLimit} songs.").queue()
                    return
                }

                if (track !is TwitchStreamAudioTrack && track !is BeamAudioTrack) {
                    if (track.duration > Bot.CONFIG.durationLimit.toMillis()) {
                        context.send().error("The track can not exceed ${Bot.CONFIG.durationLimitText}.").queue()
                        return
                    }
                }

                track.userData = context.member

                scheduler.queue(track)

                context.send().embed("Music Queue") {
                    color { Bot.CONFIG.musicColor }
                    description { "Added __**[${track.info.title}](${track.info.uri})**__ to queue." }
                    footer { footnote }
                }.action().queue()
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if (playlist.isSearchResult) {
                    trackLoaded(playlist.tracks.first())
                    return
                }

                if (!guild.selfMember.voiceState.inVoiceChannel()) {
                    if (!context.member.voiceState.inVoiceChannel()) {
                        context.send().error("You left the channel before the track is loaded.").queue()

                        // Track is not supposed to load and the queue is empty
                        // destroy player
                        if (scheduler.queue.isEmpty()) {
                            Bot.getPlayers().destroy(id)
                        }
                        return
                    }
                    openAudioConnection(context.member.voiceState.channel, context)
                }

                val tracks = playlist.tracks

                var added = 0
                for (track in tracks) {
                    if (scheduler.queue.size + 1 >= Bot.CONFIG.queueLimit) {
                        context.send().info("Ignored ${tracks.size - added} songs as the queue can not exceed ${Bot.CONFIG.queueLimit} songs.").queue()
                        break
                    }

                    track.userData = context.member

                    scheduler.queue(track)
                    added++
                }

                context.send().embed("Music Queue") {
                    setColor(Bot.CONFIG.musicColor)
                    setDescription("Added `$added` tracks to queue from playlist `${playlist.name}`.")
                    footer { footnote }
                }.action().queue()
            }

            override fun noMatches() {
                // No track found and queue is empty
                // destroy player
                if (scheduler.queue.isEmpty()) {
                    Bot.getPlayers().destroy(id)
                }
                context.send().error("Nothing found by `$trackUrl`.").queue()
            }

            override fun loadFailed(e: FriendlyException) {
                context.send().exception(e).queue()
            }
        })
    }
}
