package xyz.gnarbot.gnar.commands.executors.media

import xyz.gnarbot.gnar.utils.b
import xyz.gnarbot.gnar.utils.link
import org.json.JSONException
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.YouTube
import java.awt.Color

@Command(
        aliases = arrayOf("youtube"),
        usage = "-query...",
        description = "Search and get a YouTube video."
)
class YoutubeCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Gotta put something to search YouTube.").queue()
            return
        }

        try {
            val query = args.joinToString("+")

            val results = YouTube.search(query, 3)

            if (results.isEmpty()) {
                context.send().error("No search results for `$query`.").queue()
                return
            }
            var firstUrl: String? = null

            context.send().embed {
                setAuthor("YouTube Results", "https://www.youtube.com", "https://s.ytimg.com/yts/img/favicon_144-vflWmzoXw.png")
                thumbnail = "https://gnarbot.xyz/assets/img/youtube.png"
                color = Color(141, 20, 0)

                description {
                    buildString {
                        for (result in results) {

                            val title = result.title
                            val desc = result.description
                            val url = result.url

                            if (firstUrl == null) {
                                firstUrl = url
                            }

                            appendln(b(title link url)).appendln(desc)
                        }
                    }
                }
            }.action().queue()

            context.send().text("**First Video:** $firstUrl").queue()
        } catch (e: JSONException) {
            context.send().error("Unable to get YouTube results.").queue()
            e.printStackTrace()
        } catch (e: NullPointerException) {
            context.send().error("Unable to get YouTube results.").queue()
            e.printStackTrace()
        }
    }
}



