package xyz.gnarbot.gnar.commands.media

import org.json.JSONObject
import xyz.gnarbot.gnar.commands.*

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

@Command(aliases = ["dogs", "dog", "pooch", "bork", "bark", "woof"], description = "Grab random dogs for you.")
@BotInfo(id = 102, category = Category.MEDIA)
class DogCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        try {
            val br = BufferedReader(InputStreamReader(URL("https://dog.ceo/api/breeds/image/random").openStream()))
            val jso = JSONObject(br.readLine())
            context.send().embed()
                    .setImage(jso.getString("message"))
                    .action().queue()
        } catch (e: Exception) {
            context.send().error("Unable to find dogs to sooth the darkness of your soul.").queue()
            e.printStackTrace()
        }

    }
}
