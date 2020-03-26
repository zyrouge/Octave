package xyz.gnarbot.gnar.commands.admin

import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.utils.DiscordBotsVotes
import javax.script.*

@Command(
        aliases = ["eval"],
        description = "Run Kotlin scripts."
)
@BotInfo(
        id = 35,
        admin = true,
        category = Category.NONE
)
class EvalCommand : CommandExecutor() {
    private val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine

    override fun execute(context: Context, label: String, args: Array<String>) {
        val script = context.message.contentRaw.substringAfter("eval").trim()

        if (script.isEmpty()) {
            return context.send().error("Script can not be empty.").queue()
        }

        val bindings = mapOf(
            "ctx" to context,
            "jda" to context.jda,
            "sm" to context.jda.shardManager!!,
            "bot" to Bot.getInstance()
        )

        val bindString = bindings.map { "val ${it.key} = bindings[\"${it.key}\"] as ${it.value.javaClass.kotlin.qualifiedName}" }.joinToString("\n")
        val bind = engine.createBindings()
        bind.putAll(bindings)

        try {
            val result = engine.eval("$bindString\n${script}", bind)
                ?: return context.message.addReaction("👌").queue()

            context.send().text("```\n${result.toString().take(1950)}```").queue()
        } catch (e: Exception) {
            context.send().text("An exception occurred.\n```\n${e.localizedMessage}```").queue()
        }
    }
}