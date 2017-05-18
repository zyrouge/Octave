package xyz.gnarbot.gnar.commands.executors.general

import org.mariuszgromada.math.mxparser.Expression
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.b
import xyz.gnarbot.gnar.utils.code
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

            val script = if (args.size == 1) {
                args[0]
            } else {
                args.joinToString(" ")
            }

            field("Expressions") {
                code {
                    script
                }
            }

            val exp = Expression(script)

            if (exp.checkSyntax()) {
                val result = exp.calculate()

                field("Result", true) {
                    b(result)
                }
                field("Computing Time", true) {
                    "${exp.computingTime} seconds"
                }
            } else {
                field("Error") {
                    exp.errorMessage
                }
                color = Color.RED
            }
        }.action().queue()
    }
}