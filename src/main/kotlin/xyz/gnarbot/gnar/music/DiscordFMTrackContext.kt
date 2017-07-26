package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.utils.DiscordFMLibraries

class DiscordFMTrackContext(
        val station: String,
        requester: Long,
        requestedChannel: Long
) : TrackContext(requester, requestedChannel) {
    fun loadRandomTrack(musicManager: MusicManager) {
        DiscordFMLibraries.getRandomSong(station)?.let {
            MusicManager.playerManager.loadItemOrdered(this, it, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    musicManager.scheduler.queue.offer(track)
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    trackLoaded(playlist.tracks.first())
                }

                override fun noMatches() {
                    // Already confirmed that the list is empty.
                    Bot.getPlayers().destroy(musicManager.guild)
                }

                override fun loadFailed(exception: FriendlyException) {
                    // Already confirmed that the list is empty.
                    Bot.getPlayers().destroy(musicManager.guild)
                }
            })
        }
    }
}