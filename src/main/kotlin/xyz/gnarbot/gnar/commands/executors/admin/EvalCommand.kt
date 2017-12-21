package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.BotLoader
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.utils.DiscordBotsVotes
import javax.script.*

@Command(
        aliases = ["eval"],
        description = "Run Groovy scripts."
)
@BotInfo(
        id = 35,
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
            getBindings(ScriptContext.ENGINE_SCOPE).apply {
                put("context", context)

                if (args[0] == "gv") {
                    put("Bot", BotLoader::class.java)
                    put("DiscordBotsVotes", DiscordBotsVotes::class.java)
                }
            }
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