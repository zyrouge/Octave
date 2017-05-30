package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.entities.MessageEmbed
import xyz.avarel.aje.AJEException
import xyz.avarel.aje.Expression
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.code
import java.awt.Color
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Command(aliases = arrayOf("math"), usage = "(expression)", description = "Calculate fancy math expressions.")
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

            try {
                field("Expressions") {
                    code {
                        script
                    }
                }

                val expr = exp.compile()

                field("AST") {
                    code {
                        val ast = buildString {
                            expr.ast(this, "", true)
                        }
                        if (ast.length > MessageEmbed.VALUE_MAX_LENGTH / 2) {
                            "AST can not be displayed."
                        } else {
                            ast
                        }
                    }
                }



                field("Result") {
                    code {
                        CompletableFuture.supplyAsync(expr::compute)
                                .get(500, TimeUnit.MILLISECONDS).toString()
                    }
                }
            } catch (e : AJEException) {
                field("Error") {
                    e.message
                }
                color = Color.RED
            } catch (e : TimeoutException) {
                field("Error") {
                    "Script took too long to execute."
                }
                color = Color.RED
            }
        }.action().queue()
    }
}