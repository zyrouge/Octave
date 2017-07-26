package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.*
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.CommandRegistry
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.DiscordFMLibraries
import xyz.gnarbot.gnar.utils.TrackContext

import java.util.Collections
import java.util.LinkedList
import java.util.Queue

class TrackScheduler
/**
 * @param player The audio player this scheduler uses
 */
(private val musicManager: MusicManager, private val player: AudioPlayer) : AudioEventAdapter() {

    val queue: Queue<AudioTrack>
    var lastTrack: AudioTrack? = null
        private set
    var repeatOption = RepeatOption.NONE

    init {
        this.queue = LinkedList<AudioTrack>()
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.

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

            if (musicManager.discordFMTrack !== "") {

            }

            Bot.getPlayers().destroy(musicManager.guild)
            return
        }

        player.startTrack(queue.poll(), false)
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        this.lastTrack = track

        if (endReason!!.mayStartNext) {
            when (repeatOption) {
                RepeatOption.SONG -> {
                    val newTrack = lastTrack!!.makeClone()
                    newTrack.userData = lastTrack!!.userData
                    player!!.startTrack(newTrack, false)
                }
                RepeatOption.QUEUE -> {
                    run {
                        val newTrack = lastTrack!!.makeClone()
                        newTrack.userData = lastTrack!!.userData
                        queue.offer(newTrack)
                    }
                    nextTrack()
                }
                RepeatOption.NONE -> nextTrack()
            }
        } else {
            Bot.getPlayers().destroy(musicManager.guild)
        }
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
        musicManager.guild.getTextChannelById(track!!.getUserData(TrackContext::class.java).requestedChannel)
                .sendMessage("**ERROR:** The track " + track.info.title + " is stuck longer than "
                        + thresholdMs + "ms threshold."
                ).queue()
    }

    override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
        musicManager.guild.getTextChannelById(track!!.getUserData(TrackContext::class.java).requestedChannel)
                .sendMessage(
                        "**ERROR:** The track " + track.info.title + " encountered an exception.\n"
                                + "Severity: " + exception!!.severity + "\n"
                                + "Details: " + exception.message
                ).queue()
    }

    fun shuffle() {
        Collections.shuffle(queue as List<*>)
    }
}
