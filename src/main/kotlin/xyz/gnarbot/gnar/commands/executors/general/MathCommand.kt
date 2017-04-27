package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.b
import org.apache.commons.lang3.StringUtils
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.hexav.aje.AJEException
import xyz.hexav.aje.ExpressionBuilder
import java.awt.Color

@Command(aliases = arrayOf("math"), usage = "(expression)", description = "Calculate fancy math expressions.")
class MathCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Please provide a math expression.").queue()
            return
        }

        context.send().embed("Math") {
            color = BotConfiguration.ACCENT_COLOR

            val exp = ExpressionBuilder()
            val lines = Utils.stringSplit(StringUtils.join(args, ' '), ';')
            lines.forEach { exp.addLine(it) }

            field("Expressions", true) {
                b(lines.map(String::trim).joinToString("\n"))
            }

            try {
                val results = exp.build().evalList()

                field("Result", true) {
                    b(results.contentToString())
                }
            } catch (e: AJEException) {
                field("Error", true) {
                    e.message
                }
                color = Color.RED
            }
        }.rest().queue()
    }
}