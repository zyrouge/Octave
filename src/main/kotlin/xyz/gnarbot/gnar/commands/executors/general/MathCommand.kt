package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.entities.MessageEmbed
import xyz.avarel.aje.Expression
import xyz.avarel.aje.exceptions.AJEException
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.code
import java.awt.Color
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Command(
        aliases = arrayOf("math"),
        usage = "(expression)",
        description = "Calculate fancy math expressions."
)
class MathCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Please provide a math expression.").queue()
            return
        }

        context.send().embed("Math") {
            val script = if (args.size == 1) {
                args[0]
            } else {
                args.joinToString(" ")
            }

            val exp = Expression(script)
            exp.parserFlag.setAllowRanges(false)
            exp.parserFlag.setAllowLoops(false)

            try {
                field("Script") {
                    code {
                        script
                    }
                }

                val expr = exp.compile()

                var ast: String? = null

                val result = CompletableFuture.supplyAsync {
                    ast = buildString {
                        expr.ast(this, "", true)
                    }

                    expr.compute()
                }.get(500, TimeUnit.MILLISECONDS)

                field("AST") {
                    code {
                        if (ast == null || ast!!.length > MessageEmbed.VALUE_MAX_LENGTH / 2) {
                            "AST can not be displayed."
                        } else {
                            ast!!
                        }
                    }
                }

                field("Result") {
                    code {
                        result.toString()
                    }
                }
            } catch (e : AJEException) {
                field("Error") {
                    e.message
                }
                setColor(Color.RED)
            } catch (e : ExecutionException) {
                field("Error") {
                    e.cause?.message ?: e.message
                }
                setColor(Color.RED)
            } catch (e : TimeoutException) {
                field("Error") {
                    "Script took too long to execute."
                }
                setColor(Color.RED)
            }
        }.action().queue()
    }
}