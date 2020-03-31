package xyz.gnarbot.gnar.commands.`fun`

import io.sentry.Sentry
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.utils.URIBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["ascii"],
        usage = "(words...)",
        description = "ASCII text art!"
)
@BotInfo(
        id = 39,
        category = Category.FUN
)
class ASCIICommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            context.bot.commandDispatcher.sendHelp(context, info)
            return
        }

        try {
            val query = StringUtils.join(args, " ")

            if (query.length > 15) {
                context.send().error("The query has too many characters. 15 at most.").queue()
                return
            }

            val document = Jsoup.connect(
                    URIBuilder("http://artii.herokuapp.com/make").addParameter("text", query).toString()).get()

            val element = document.getElementsByTag("body")[0]

            context.send().info("```\n${getText(element)}```").queue()

        } catch (e: Exception) {
            context.send().error("Unable to generate ASCII art.").queue()
            Sentry.capture(e)
            e.printStackTrace()
        }
    }

    private fun getText(cell: Element): String? {
        var text: String? = null
        val childNodes = cell.childNodes()
        if (childNodes.size > 0) {
            val childNode = childNodes[0]
            if (childNode is TextNode) {
                text = childNode.wholeText
            }
        }
        if (text == null) {
            text = cell.text()
        }

        text = text?.split('\n')?.let {
            buildString {
                it.filterNot(String::isNullOrBlank)
                        .forEach { appendln(it) }
            }
        }

        return text
    }
}

