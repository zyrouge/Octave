package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.executors.music.embedUri
import xyz.gnarbot.gnar.utils.respond
import java.util.*

class TrackScheduler(private val musicManager: MusicManager, private val player: AudioPlayer) : AudioEventAdapter() {
    val queue: Queue<AudioTrack> = LinkedList<AudioTrack>()
    var repeatOption = RepeatOption.NONE
    var lastTrack: AudioTrack? = null
        private set

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    fun queue(track: AudioTrack) {
        if (!player.startTrack(track, true)) {
            queue.offer(track)
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    fun nextTrack() {
        if (queue.isEmpty()) {
            musicManager.discordFMTrack?.let {
                it.nextDiscordFMTrack(musicManager)
                return
            }

            Bot.getPlayers().destroy(musicManager.guild)
            return
        }

        val track = queue.poll()
        player.startTrack(track, false)

        if (Bot.getOptions().ofGuild(musicManager.guild).isAnnounce) {
            announceNext(track)
        }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        this.lastTrack = track

        if (endReason.mayStartNext) {
            when (repeatOption) {
                RepeatOption.SONG -> {
                    val newTrack = track.makeClone().also { it.userData = track.userData }
                    player.startTrack(newTrack, false)
                }
                RepeatOption.QUEUE -> {
                    val newTrack = track.makeClone().also { it.userData = track.userData }
                    queue.offer(newTrack)
                    nextTrack()
                }
                RepeatOption.NONE -> nextTrack()
            }
        }
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {
        musicManager.guild.getTextChannelById(track.getUserData(TrackContext::class.java).requestedChannel)
                .respond()
                .error("The track ${track.info.title} is stuck longer than ${thresholdMs}ms threshold.")
                .queue()
    }

    override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
        musicManager.guild.getTextChannelById(track.getUserData(TrackContext::class.java).requestedChannel)
                .respond()
                .error(
                        "The track ${track.info.title} encountered an exception.\n" +
                        "Severity: ${exception.severity}\n" +
                        "Details: ${exception.message}"
                ).queue()
    }

    fun announceNext(track: AudioTrack) {
        musicManager.currentRequestChannel?.let {
            it.respond().embed("Music Playback") {
                desc {
                    buildString {
                        append("Now playing __**[").append(track.info.title)
                        append("](").append(track.info.embedUri).append(")**__")

                        track.getUserData(TrackContext::class.java)?.requester?.let(musicManager.guild::getMemberById)?.let {
                            append(" requested by ")
                            append(it.asMention)
                        }

                        append(".")
                    }
                }
            }.action().queue()
        }
    }

    fun shuffle() = Collections.shuffle(queue as List<*>)
}
