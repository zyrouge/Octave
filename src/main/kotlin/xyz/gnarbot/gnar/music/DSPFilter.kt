package xyz.gnarbot.gnar.music

import com.github.natanbc.lavadsp.distortion.DistortionPcmAudioFilter
import com.github.natanbc.lavadsp.karaoke.KaraokePcmAudioFilter
import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter
import com.github.natanbc.lavadsp.tremolo.TremoloPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer

class DSPFilter(private val player: AudioPlayer) {
    // Distortion properties
    var distortionEnable = false
        set(value) {
            field = value
            applyFilters()
        }
    var dCosOffset = 0f
        set(value) {
            field = value
            applyFilters()
        }
    var dCosScale = 1f
        set(value) {
            field = value
            applyFilters()
        }
    var dOffset = 0f
        set(value) {
            field = value
            applyFilters()
        }
    var dScale = 1f
        set(value) {
            field = value
            applyFilters()
        }
    var dSinOffset = 0f
        set(value) {
            field = value
            applyFilters()
        }
    var dSinScale = 1f
        set(value) {
            field = value
            applyFilters()
        }
    var dTanOffset = 0f
        set(value) {
            field = value
            applyFilters()
        }
    var dTanScale = 1f
        set(value) {
            field = value
            applyFilters()
        }

    // Karaoke properties
    var karaokeEnable = false
        set(value) {
            field = value
            applyFilters()
        }
    var kLevel = 1.0f
        set(value) {
            field = value
            applyFilters()
        }
    var kFilterBand = 220f
        set(value) {
            field = value
            applyFilters()
        }
    var kFilterWidth = 100f
        set(value) {
            field = value
            applyFilters()
        }

    // Timescale properties
    val timescaleEnable: Boolean
        get() = tsSpeed != 1.0 || tsPitch != 1.0 || tsRate != 1.0

    var tsSpeed = 1.0
        set(value) {
            field = value
            applyFilters()
        }
    var tsPitch = 1.0
        set(value) {
            field = value
            applyFilters()
        }
    var tsRate = 1.0
        set(value) {
            field = value
            applyFilters()
        }

    // Tremolo properties
    var tremoloEnable = false
        set(value) {
            field = value
            applyFilters()
        }
    var tDepth = 0.5f
        set(value) {
            field = value
            applyFilters()
        }
    var tFrequency = 2f
        set(value) {
            field = value
            applyFilters()
        }

    fun applyFilters() {
        // TODO: Support bass boost
        player.setFilterFactory { track, format, output ->
            val filters = mutableListOf<AudioFilter>()

            if (distortionEnable) {
                val filter = DistortionPcmAudioFilter(output, format.channelCount).apply {
                    cosOffset = dCosOffset
                    cosScale = dCosScale
                    offset = dOffset
                    scale = dScale
                    sinOffset = dSinOffset
                    sinScale = dSinScale
                    tanOffset = dTanOffset
                    tanScale = dTanScale
                }
                filters.add(filter)
            }

            if (karaokeEnable) {
                val filter = KaraokePcmAudioFilter(output, format.channelCount, format.sampleRate).apply {
                    level = kLevel
                    filterBand = kFilterBand
                    filterWidth = kFilterWidth
                }
                filters.add(filter)
            }

            if (timescaleEnable) {
                val filter = TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate).apply {
                    pitch = tsPitch
                    speed = tsSpeed
                    rate = tsRate
                }
                filters.add(filter)
            }

            if (tremoloEnable) {
                val filter = TremoloPcmAudioFilter(output, format.channelCount, format.sampleRate).apply {
                    depth = tDepth
                    frequency = tFrequency
                }
                filters.add(filter)
            }

            return@setFilterFactory filters.toList()
        }
    }

    fun clearFilters() {
        player.setFilterFactory(null)
    }

}
