package xyz.gnarbot.gnar.music

import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln
import java.awt.Color

class MusicLimitException : Exception() {
    fun sendToContext(context: Context) {
        context.send().embed("Maximum Capacity") {
            color { Color.ORANGE }
            description {
                buildString {
                    append("Music is currently at maximum capacity, please try again later.").ln()
                    append("Please consider donating to our **[Patreon](https://www.patreon.com/gnarbot)** to help us with hosting costs.")
                }
            }
        }.action().queue()
    }
}