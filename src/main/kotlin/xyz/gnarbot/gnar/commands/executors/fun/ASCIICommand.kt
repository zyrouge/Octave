package xyz.gnarbot.gnar.commands.executors.`fun`

import net.dv8tion.jda.core.entities.Message
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import xyz.gnarbot.gnar.BotConfig
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Utils

@Command(
        aliases = arrayOf("ascii"),
        usage = "-string",
        description = "ASCII text art!"
)
class ASCIICommand : CommandExecutor() {
    override fun execute(message: Message, args: Array<String>) {
        if (args.isEmpty()) {
            message.respond().error("Please provide a query.").queue()
            return
        }

        try {
            val query = StringUtils.join(args, "+")

            if (query.length > 15) {
                message.respond().error("The query has too many characters. 15 at most.").queue()
                return
            }

            val document = Jsoup.connect("http://artii.herokuapp.com/make?text=$query").get()

            val element = document.getElementsByTag("body")[0]

            message.respond().embed("ASCII Text") {
                color = BotConfig.COLOR
                description = "```\n${getText(element)}```"
            }.rest().queue()

        } catch (e: Exception) {
            message.respond().error("Unable to generate ASCII art.").queue()
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

        text = text?.let { Utils.stringSplit(it, '\n') }?.let {
            buildString {
                it.filterNot(String::isNullOrBlank)
                        .forEach { appendln(it) }
            }
        }

        return text
    }
}

