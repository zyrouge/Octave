package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.MessageBuilder
import org.scilab.forge.jlatexmath.TeXConstants
import org.scilab.forge.jlatexmath.TeXFormula
import xyz.avarel.aljava.TexElement
import xyz.avarel.aljava.lexer.Lexer
import xyz.avarel.aljava.parser.Parser
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandDispatcher
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Command(
        id = 80,
        usage = "(x+y)^2` or `3x + 4 = 31",
        aliases = arrayOf("math"),
        description = "Do mathematical stuff."
)
class MathCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            CommandDispatcher.sendHelp(context, info)
            return
        }

        val str = args.joinToString(" ")

        val finalTex = if (str.contains("=")) {
            val eq = Parser(Lexer(str)).parseEquation()

            val eqTex = eq.toTex().replace("=", "&=")

            val simpleEq = eq.simplify()
            val simpleEqTex = if (simpleEq.toString() != eq.toString()) {
                simpleEq.toTex().replace("=", "&=")
            } else null

            val results = eq.solveFor("x")
            val resultsTex = "x &= " + if (results.size == 1) {
                results[0].let {
                    if (it is TexElement) it.toTex() else it.toString()
                }
            } else {
                results.joinToString(", ", "[", "]") {
                    if (it is TexElement) it.toTex() else it.toString()
                }
            }

            "\\begin{align}${when {
                eqTex == resultsTex -> resultsTex
                simpleEqTex == null -> "$eqTex\\\\$resultsTex"
                else -> "$eqTex\\\\$simpleEqTex\\\\$resultsTex"
            }}\\end{align}"
        } else {
            val expr = Parser(Lexer(str)).parse()

            val exprTex = expr.toTex()

            val simpleExpr = expr.simplify()
            val simpleExprTex = if (simpleExpr.toString() != expr.toString()) {
                simpleExpr.toTex()
            } else null

            "\\begin{align}${if (simpleExprTex == null) {
                exprTex
            } else {
                "$exprTex\\\\$simpleExprTex"
            }}\\end{align}"
        }

        val file: File = File("temp.png").also(File::deleteOnExit)
        writeTexToFile(finalTex, file)

        context.channel.sendFile(file, file.name, MessageBuilder().setEmbed(
                context.send().embed {
                    image { "attachment://${file.name}" }
                }.build()).build()
        ).queue()
    }

    private fun writeTexToFile(tex: String, file: File) {
        val formula = TeXFormula(tex)
        val ti = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 30f)
        val b = BufferedImage(ti.iconWidth, ti.iconHeight, BufferedImage.TYPE_4BYTE_ABGR)

        b.createGraphics().let {
            it.paint = Color.WHITE
            it.fillRect(0, 0, b.width, b.height)
            ti.paintIcon(null, it, 0, 0)
        }

        ImageIO.write(b, "png", file)
    }
}
