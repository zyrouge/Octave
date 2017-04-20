package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioTrack
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.guilds.GuildData

class MusicManager(guildData: GuildData, val playerManager: AudioPlayerManager) {

    /**
     * @return Audio player for the guild.
     */
    val player: AudioPlayer = playerManager.createPlayer()

    /**
     * @return Track scheduler for the player.
     */
    val scheduler: TrackScheduler = TrackScheduler(guildData, player)

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    val sendHandler: AudioPlayerSendHandler = AudioPlayerSendHandler(player)

    /**
     * @return Voting cooldown.
     */
    var lastVoteTime: Long = 0L

    /**
     * @return Whether there is a vote to skip the song or not.
     */
    var isVotingToSkip = false

    init {
        player.addListener(scheduler)
    }

    fun loadAndPlay(message: Message, trackUrl: String) {
        playerManager.loadItemOrdered(this, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                if (scheduler.queue.size >= BotConfiguration.QUEUE_LIMIT) {
                    message.send().error("The queue can not exceed ${BotConfiguration.QUEUE_LIMIT} songs.").queue(null) {
                        if (it is PermissionException) {
                            message.send().text("The queue can not exceed ${BotConfiguration.QUEUE_LIMIT} songs.").queue()
                        }
                    }
                    return
                }

                if (track !is TwitchStreamAudioTrack && track !is BeamAudioTrack) {
                    if (track.duration > BotConfiguration.DURATION_LIMIT.toMillis()) {
                        message.send().error("The track can not exceed ${BotConfiguration.DURATION_LIMIT_TEXT}.").queue(null) {
                            if (it is PermissionException) {
                                message.send().text("The track can not exceed ${BotConfiguration.DURATION_LIMIT_TEXT}.").queue()
                            }
                        }
                        return
                    }
                }

                scheduler.queue(track)

                message.send().embed("Music Queue") {
                    color = BotConfiguration.MUSIC_COLOR
                    description = "Added __**[${track.info.title}](${track.info.uri})**__ to queue."
                }.rest().queue()
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                val tracks = playlist.tracks

                var added = 0
                for (track in tracks) {
                    if (scheduler.queue.size >= BotConfiguration.QUEUE_LIMIT) {
                        message.send().info("Ignored ${tracks.size - added} songs as the queue can not exceed ${BotConfiguration.QUEUE_LIMIT} songs.").queue(null) {
                            if (it is PermissionException) {
                                message.send().text("Ignored ${tracks.size - added} songs as the queue can not exceed ${BotConfiguration.QUEUE_LIMIT} songs.").queue()
                            }
                        }
                        break
                    }

                    scheduler.queue(track)
                    added++
                }

                message.send().embed("Music Queue") {
                    color = BotConfiguration.MUSIC_COLOR
                    description = "Added `$added` tracks to queue from playlist `${playlist.name}`."
                }.rest().queue(null) {
                    if (it is PermissionException) {
                        message.send().text("Added `$added` tracks to queue from playlist `${playlist.name}`.").queue()
                    }
                }
            }

            override fun noMatches() {
                message.send().error("Nothing found by `$trackUrl`.").queue(null) {
                    if (it is PermissionException) {
                        message.send().text("Nothing found by `$trackUrl`.").queue()
                    }
                }
            }

            override fun loadFailed(e: FriendlyException) {
                message.send().error("**Exception**: `${e.message}`").queue(null) {
                    if (it is PermissionException) {
                        message.send().text("**Exception**: `${e.message}`").queue()
                    }
                }
            }
        })
    }
}
