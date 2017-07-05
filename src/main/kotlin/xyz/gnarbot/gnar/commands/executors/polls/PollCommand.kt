package xyz.gnarbot.gnar.commands.executors.polls

import net.dv8tion.jda.core.EmbedBuilder
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.desc
import java.util.concurrent.TimeUnit

@Command(
        aliases = arrayOf("poll"),
        usage = "(option 1);(option 2);...",
        description = "Create a poll.",
        cooldown = 10000 // 2 minutes
)
class PollCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val options = args.joinToString(" ").split(';').map(String::trim)

        if (options.size <= 1) {
            context.send().error("Please offer more options for the poll. `_poll (option 1);(option 2);...`").queue()
            return
        }
        
        context.send().embed("Poll") {
            desc { "Vote through clicking the reactions on the choices below!" }
            field("Options") {
                buildString {
                    options.forEachIndexed { index, option ->
                        appendln("${'\u0030' + index}\u20E3 **$option**")
                    }
                }
            }
            footer { "Results will be final in 2 minutes." }
        }.action().queue {
            for (index in 0..options.size - 1) {
                it.addReaction("${'\u0030' + index}\u20E3").queue()
            }

            it.editMessage(EmbedBuilder(it.embeds[0]).apply {
                desc { "Voting has ended! Check the results in the newer messages!" }
                clearFields()
            }.build()).queueAfter(2, TimeUnit.MINUTES) {
                context.send().embed("Poll Results") {
                    desc { "Voting has ended! Here are the results!" }

                    var topVotes = 0
                    val winners = mutableListOf<Int>()

                    field("Results") {
                        buildString {
                            it.reactions.forEach { reaction ->
                                val value = reaction.emote.name[0] - '\u0030'
                                if (value !in 0..options.size - 1) return@forEach

                                options[value].let {
                                    appendln("${reaction.emote.name} **$it** — __${reaction.count - 1} Votes__")

                                    if (reaction.count - 1 > topVotes) {
                                        winners.clear()
                                        topVotes = reaction.count - 1
                                        winners += value
                                    } else if (reaction.count - 1 == topVotes) {
                                        winners += value
                                    }
                                }
                            }
                        }
                    }

                    it.clearReactions().queue()

                    field("Winner") {
                        winners.joinToString(prefix = "**", postfix = "**") { options[it] }
                    }
                }.action().queue()
            }
        }
    }
}
