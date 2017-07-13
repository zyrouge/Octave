package xyz.gnarbot.gnar.commands.executors.media

import org.apache.http.client.utils.URIBuilder
import org.jsoup.Jsoup
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.b
import xyz.gnarbot.gnar.utils.link
import xyz.gnarbot.gnar.utils.ln
import java.io.IOException

@Command(
        id = 50,
        aliases = arrayOf("google"),
        usage = "(query...)",
        description = "Who needs browsers!?",
        category = Category.MEDIA
)
class GoogleCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Put something to search Google.").queue()
            return
        }

        try {
            val query = args.joinToString(" ")

            val url = URIBuilder("http://www.google.com/search").addParameter("q", query).build().toString()

            val blocks = Jsoup.connect(url)
                    .userAgent("Gnar")
                    .get()
                    .select(".g")

            if (blocks.isEmpty()) {
                context.send().error("No search results for `$query`.").queue()
                return
            }

            context.send().embed {
                setAuthor("Google Results", "https://www.google.com/", "https://www.google.com/favicon.ico")
                setThumbnail("https://gnarbot.xyz/assets/img/google.png")

                desc {
                    var count = 0

                    buildString {
                        for (block in blocks) {
                            if (count >= 3) break

                            val list = block.select(".r>a")

                            if (list.isEmpty()) continue

                            val entry = list[0]
                            val title = entry.text()
                            val url1 = entry.absUrl("href").replace(")", "\\)")
                            var desc: String? = null

                            val st = block.select(".st")
                            if (!st.isEmpty()) desc = st[0].text()

                            append(b(title link url1)).ln().append(desc).ln()

                            count++
                        }
                    }
                }
            }.action().queue()
        } catch (e: IOException) {
            context.send().error("Caught an exception while trying to Google stuff.").queue()
            e.printStackTrace()
        }
    }
}
