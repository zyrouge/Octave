package xyz.gnarbot.gnar.commands.executors.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import java.util.regex.Pattern

internal val PLAY_MESSAGE = "\uD83C\uDFB6 `_play (song/url)` in a voice channel to start playing some music!"

private val markdownCharacters: Pattern = Pattern.compile("[*_`~)]")
val AudioTrackInfo.embedTitle: String get() = markdownCharacters.matcher(title).replaceAll("")