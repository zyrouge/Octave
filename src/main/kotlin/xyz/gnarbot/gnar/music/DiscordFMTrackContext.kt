package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.gnarbot.gnar.Bot

class DiscordFMTrackContext(
        private val bot: Bot,
        val station: String,
        requester: Long,
        requestedChannel: Long
) : TrackContext(requester, requestedChannel) {
    companion object {
        @JvmStatic
        val errorTolerance = 3
    }

    fun nextDiscordFMTrack(musicManager: MusicManager, errorDepth: Int = 0) {
        /*val randomSong = bot.discordFM.getRandomSong(station) ?: return nextDiscordFMTrack(musicManager, errorDepth + 1)

        MusicManager.playerManager.loadItemOrdered(this, randomSong, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                track.userData = this@DiscordFMTrackContext
                musicManager.scheduler.queue(track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                trackLoaded(playlist.tracks.first())
            }

            override fun noMatches() {
                // Already confirmed that the list is empty.
                bot.players.destroy(musicManager.guild)
            }

            override fun loadFailed(exception: FriendlyException) {
                if (errorDepth >= errorTolerance) {
                    // Already confirmed that the list is empty.
                    bot.players.destroy(musicManager.guild)
                    return
                } else {
                    nextDiscordFMTrack(musicManager, errorDepth + 1)
                }
            }
        })
        */
    }
}