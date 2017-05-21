package xyz.gnarbot.gnar.commands.executors.general

import xyz.avarel.aje.AJEException
import xyz.avarel.aje.Expression
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
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
            color = context.bot.config.accentColor

            val script = if (args.size == 1) {
                args[0]
            } else {
                args.joinToString(" ")
            }

            val exp = Expression(script)

            try {
                field("Expressions") {
                    code {
                        script
                    }
                }

                val expr = exp.compile()

                field("AST") {
                    code {
                        buildString {
                            expr.ast(this, "", true)
                        }
                    }
                }

                field("Result") {
                    code {
                        expr.compute().toString()
                    }
                }
            } catch (e : AJEException) {
                field("Error") {
                    e.message
                }
                color = Color.RED
            }
        }.action().queue()
    }
}