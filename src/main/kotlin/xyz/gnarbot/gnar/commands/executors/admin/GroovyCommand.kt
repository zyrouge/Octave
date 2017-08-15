package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import javax.script.*

@Command(
        id = 35,
        aliases = arrayOf("gv"),
        description = "Run Groovy scripts.",
        admin = true,
        category = Category.NONE
)
class GroovyCommand : CommandExecutor() {
    companion object {
        val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByName("groovy")
    }

    override fun execute(context: Context, label: String, args: Array<String>) {
        val script = args.joinToString(" ")
        if (script.isEmpty()) {
            context.send().error("Script can not be empty.").queue()
            return
        }

        val scope = SimpleScriptContext()

        scope.getBindings(ScriptContext.ENGINE_SCOPE).put("context", context)

        val result = try {
            scriptEngine.eval(script, scope)
        } catch (e: ScriptException) {
            return context.send().exception(e).queue()
        }

        if (result != null) {
            context.send().text(result.toString()).queue()
        } else {
            context.message.addReaction("\uD83D\uDC4C").queue()
        }
    }
}