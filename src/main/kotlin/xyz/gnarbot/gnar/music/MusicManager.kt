package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioTrack
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.VoiceChannel
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ResponseBuilder
import xyz.gnarbot.gnar.utils.TrackContext
import xyz.gnarbot.gnar.utils.ln
import java.util.concurrent.TimeUnit

// TEMPORARY TRIAL USING GUILD INSTANCE. Hey, guild shouldn't be invalidated midway through playing right?
// Should prevent NPE when a guild kicks the bot along with other headaches.
// In case of disconnect where JDA possibly might invalidate its cache, it's handled by the BotListener.java
class MusicManager(val guild: Guild) {
    companion object {
        val playerManager: AudioPlayerManager = DefaultAudioPlayerManager().also {
            it.registerSourceManager(YoutubeAudioSourceManager())
            it.registerSourceManager(SoundCloudAudioSourceManager())
            it.registerSourceManager(BandcampAudioSourceManager())
            it.registerSourceManager(VimeoAudioSourceManager())
            it.registerSourceManager(TwitchStreamAudioSourceManager())
            it.registerSourceManager(BeamAudioSourceManager())
        }

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

    var leaveTask: Job? = null

    /** @return Audio player for the guild. */
    val player: AudioPlayer = playerManager.createPlayer().also {
        it.volume = Bot.getOptions().ofGuild(guild).musicVolume
    }

    /**  @return Track scheduler for the player.*/
    val scheduler: TrackScheduler = TrackScheduler(this, player).also(player::addListener)

    /** @return Wrapper around AudioPlayer to use it as an AudioSendHandler. */
    val sendHandler: AudioPlayerSendHandler = AudioPlayerSendHandler(player)

    /**
     * @return Voting cooldown.
     */
    var lastVoteTime: Long = 0L

    val currentRequestChannel: TextChannel?
        get() {
            return (player.playingTrack ?: scheduler.lastTrack)
                    ?.getUserData(TrackContext::class.java)
                    ?.requestedChannel
                    ?.let(guild::getTextChannelById)
        }

    /**
     * @return Whether there is a vote to skip the song or not.
     */
    var isVotingToSkip = false

    /**
     * @return If the user is listening to DiscordFM
     * @type Null="no", ""=No, anything else=yes
     */
    var discordFMTrack : String = ""


    fun destroy() {
        player.destroy()
        closeAudioConnection()
    }

    fun openAudioConnection(channel: VoiceChannel, context: Context) : Boolean {
        when {
            !Bot.CONFIG.musicEnabled -> {
                context.send().error("Music is disabled.").queue()
                Bot.getPlayers().destroy(guild)
                return false
            }
            !guild.selfMember.hasPermission(channel, Permission.VOICE_CONNECT) -> {
                context.send().error("The bot can't connect to this channel due to a lack of permission.").queue()
                Bot.getPlayers().destroy(guild)
                return false
            }
            channel.userLimit != 0 && channel.members.size >= channel.userLimit -> {
                context.send().error("The bot can't join due to the user limit.").queue()
                Bot.getPlayers().destroy(guild)
                return false
            }
            else -> {
                guild.audioManager.sendingHandler = sendHandler
                guild.audioManager.openAudioConnection(channel)

                context.send().embed("Music Playback") {
                    desc { "Joining channel `${channel.name}`." }
                }.action().queue()
                return true
            }
        }
    }

    fun moveAudioConnection(channel: VoiceChannel) {
        guild.let {
            if (!guild.selfMember.voiceState.inVoiceChannel()) {
                throw IllegalStateException("Bot is not in a voice channel")
            }

            if (!guild.selfMember.hasPermission(channel, Permission.VOICE_CONNECT)) {
                currentRequestChannel?.let { requestChannel ->
                    ResponseBuilder(requestChannel).error("I don't have permission to join `${channel.name}`.").queue()
                }
                closeAudioConnection()
                return
            }

            player.isPaused = true
            it.audioManager.openAudioConnection(channel)
            player.isPaused = false

            currentRequestChannel?.let { requestChannel ->
                ResponseBuilder(requestChannel).embed("Music Playback") {
                    desc { "Moving to channel `${channel.name}`." }
                }.action().queue()
            }
        }
    }

    fun closeAudioConnection() {
        guild.let {
            it.audioManager.closeAudioConnection()
            it.audioManager.sendingHandler = null
        }
    }

    fun isAlone(): Boolean {
        return guild.selfMember.voiceState.channel?.members?.let {
            it.size == 1 && it[0] == guild.selfMember
        } ?: true
    }

    fun queueLeave() {
        leaveTask?.cancel()
        leaveTask = createLeaveTask()
        player.isPaused = true
    }

    fun cancelLeave() {
        leaveTask?.cancel()
        leaveTask = null
        player.isPaused = false
    }

    private fun createLeaveTask() = launch(CommonPool) {
        delay(30, TimeUnit.SECONDS)
        Bot.getPlayers().destroy(guild)
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
                            Bot.getPlayers().destroy(guild)
                        }
                        return
                    }
                    if (!openAudioConnection(context.member.voiceState.channel, context)) {
                        return
                    }
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

                track.userData = TrackContext(context.member.user.idLong, context.channel.idLong)

                scheduler.queue(track)

                context.send().embed("Music Queue") {
                    desc { "Added __**[${track.info.title}](${track.info.uri})**__ to queue." }
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
                            Bot.getPlayers().destroy(guild)
                        }
                        return
                    }
                    if (!openAudioConnection(context.member.voiceState.channel, context)) {
                        return
                    }
                }

                val tracks = playlist.tracks
                var ignored = 0

                var added = 0
                for (track in tracks) {
                    if (scheduler.queue.size + 1 >= Bot.CONFIG.queueLimit) {
                        ignored = tracks.size - added
                        break
                    }

                    track.userData = TrackContext(context.member.user.idLong, context.channel.idLong)

                    scheduler.queue(track)
                    added++
                }

                context.send().embed("Music Queue") {
                    desc {
                        buildString {
                            append("Added `$added` tracks to queue from playlist `${playlist.name}`.").ln()
                            if (ignored > 0) {
                                append("Ignored `$ignored` songs as the queue can not exceed `${Bot.CONFIG.queueLimit}` songs.")
                            }
                        }
                    }

                    footer { /*Alex*/footnote }
                }.action().queue()
            }

            override fun noMatches() {
                // No track found and queue is empty
                // destroy player
                if (scheduler.queue.isEmpty()) {
                    Bot.getPlayers().destroy(guild)
                }
                context.send().error("Nothing found by `$trackUrl`.").queue()
            }

            override fun loadFailed(e: FriendlyException) {
                // No track found and queue is empty
                // destroy player

                if (scheduler.queue.isEmpty()) {
                    Bot.getPlayers().destroy(guild)
                }
                context.send().exception(e).queue()
            }
        })
    }
}
