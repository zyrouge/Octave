package xyz.gnarbot.gnar.music

class DiscordFMTrackContext(
        val station: String,
        requester: Long, 
        requestedChannel: Long
) : TrackContext(requester, requestedChannel)