package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.awt.Color
import javax.script.*

@Command(
        aliases = arrayOf("js"),
        description = "Run JS scripts.",
        administrator = true,
        category = Category.NONE
)
class JavascriptCommand : CommandExecutor() {
    val blocked = arrayListOf("leave", "delete", "Guilds", "Token", "Channels", "voice",
            "remove", "ByName", "ById", "Controller", "Manager", "Permissions")

    companion object {
        val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByName("javascript")
    }

    override fun execute(context: Context, args: Array<String>) {
        val script = args.joinToString(" ")
        if (script.isNullOrEmpty()) {
            context.send().error("Script can not be empty.").queue()
            return
        }

        val scope = SimpleScriptContext()

        scope.getBindings(ScriptContext.ENGINE_SCOPE).put("context", context)

        if (blocked.any { script.contains(it, true) }) {
            context.send().error("JavaScript eval Expression may be malicious, canceling.").queue()
            return
        }

        context.send().embed("JavaScript") {
            color = BotConfiguration.ACCENT_COLOR

            field("Running", false, script)
            field("Result", false, try {
                scriptEngine.eval(script, scope)
            } catch (e: ScriptException) {
                color = Color.RED
                "The error `$e` occurred while executing the JavaScript statement."
            })
        }.action().queue()
    }

}