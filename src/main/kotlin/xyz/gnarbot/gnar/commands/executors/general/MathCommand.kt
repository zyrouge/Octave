package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.MessageBuilder
import org.scilab.forge.jlatexmath.TeXConstants
import org.scilab.forge.jlatexmath.TeXFormula
import xyz.avarel.aljava.TexElement
import xyz.avarel.aljava.lexer.Lexer
import xyz.avarel.aljava.parser.Parser
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Command(
        id = 80,
        aliases = arrayOf("math"),
        description = "Do mathematical stuff."
)
class MathCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Put something like `(x+y)^2` or `3x + 4 = 31`.").queue()
            return
        }

        val str = args.joinToString(" ")

        val file: File = File("temp.png").also(File::deleteOnExit)

        if (str.contains("=")) {
            val expr = Parser(Lexer(str)).parseEquation()
            val results = expr.solveFor("x")

            val tex = results.joinToString(", ", "[", "]") {
                if (it is TexElement) {
                    it.toTex()
                } else {
                    it.toString()
                }
            }

            writeTexToFile(tex, file)
        } else {
            val expr = Parser(Lexer(str)).parse()
            writeTexToFile(expr.simplify().toTex(), file)
        }

        context.channel.sendFile(file, file.name, MessageBuilder().setEmbed(
                context.send().embed {
                    image { "attachment://${file.name}" }
                }.build()).build()
        ).queue()
    }

    fun writeTexToFile(tex: String, file: File) {
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
