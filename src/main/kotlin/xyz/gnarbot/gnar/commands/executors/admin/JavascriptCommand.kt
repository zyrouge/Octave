package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.awt.Color
import javax.script.ScriptEngineManager
import javax.script.ScriptException

@Command(
        aliases = arrayOf("js", "runjs"),
        description = "Run JavaScript commands.",
        administrator = true,
        category = Category.NONE
)
class JavascriptCommand : CommandExecutor() {
    val blocked = arrayListOf("leave", "delete", "Guilds", "Token", "Channels", "voice",
            "remove", "ByName", "ById", "Controller", "Manager", "Permissions")

    override fun execute(context: Context, args: Array<String>) {
        val engine = ScriptEngineManager().getEngineByName("javascript")

        engine.put("context", context)
        val script = args.joinToString(" ")

        if (blocked.any { script.contains(it, true) }) {
            context.send().error("JavaScript eval Expression may be malicious, canceling.").queue()
            return
        }

        context.send().embed("JavaScript") {
            color = BotConfiguration.ACCENT_COLOR

            field("Running", false, script)
            field("Result", false, try {
                engine.eval(script)
            } catch (e: ScriptException) {
                color = Color.RED
                "The error `$e` occurred while executing the JavaScript statement."
            })
        }.action().queue()


    }
}