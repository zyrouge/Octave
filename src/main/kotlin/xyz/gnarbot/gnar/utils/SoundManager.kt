package xyz.gnarbot.gnar.utils

import io.sentry.Sentry
import java.io.File

class SoundManager {
    var map: HashMap<String, String> = HashMap()

    fun loadSounds() {
        try {
            for (s in File("/home/gnar/data/sounds").listFiles()) {
                print(s)
                map[s.name.replace(".mp3", "").replace("sounds\\", "")] = s.path
            }
        } catch (e: Exception) {
            Sentry.capture(e)
            e.printStackTrace()
        }
    }

}