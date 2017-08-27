package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import javax.script.*

@Command(
        id = 35,
        aliases = arrayOf("eval"),
        description = "Run Groovy scripts.",
        admin = true,
        category = Category.NONE
)
class EvalCommand : CommandExecutor() {
    private val scriptEngines: Map<String, ScriptEngine> = ScriptEngineManager().let {
        mapOf(
                "js" to it.getEngineByName("javascript"),
                "gv" to it.getEngineByName("groovy")
        )
    }

    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Available engines: `${scriptEngines.keys}`").queue()
            return
        }

        val scriptEngine = scriptEngines[args[0]]
        if (scriptEngine == null) {
            context.send().error("Not a valid engine: `${scriptEngines.keys}`.").queue()
            return
        }

        if (args.size == 1) {
            context.send().error("Script can not be empty.").queue()
            return
        }

        val script = args.copyOfRange(1, args.size).joinToString(" ")

        val scope = SimpleScriptContext().apply {
            getBindings(ScriptContext.ENGINE_SCOPE).put("context", context)
        }

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