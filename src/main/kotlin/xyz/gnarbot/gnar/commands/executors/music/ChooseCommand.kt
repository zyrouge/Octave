package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.b
import xyz.gnarbot.gnar.utils.link
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("choose", "pick"),
        usage = "[#-option]",
        description = "Pick and play music from search results.",
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class ChooseCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<out String>) {
        val manager = context.guildData.musicManager

        val botChannel = context.guild.selfMember.voiceState.channel
        val userChannel = context.guild.getMember(context.message.author).voiceState.channel

        if (botChannel != null && botChannel != userChannel) {
            context.send().error("The bot is already playing music in another channel.").queue()
            return
        }

        if (userChannel == null) {
            context.send().error("You must be in a voice channel to play music.").queue()
            return
        }

        val youtubeResultsMap = manager.youtubeResultsMap
        if (context.member !in youtubeResultsMap) {
            context.send().error("You haven't searched anything. Use `_yt (query)` and then pick your results.").queue()
            return
        }

        val (results, time) = youtubeResultsMap[context.member]!!

        if (results.isEmpty()) {
            context.send().error("Your previous searches yielded no results.").queue()
        }

        if (System.currentTimeMillis() - time > context.bot.config.searchDuration.toMillis()) {
            context.send().error("You haven't searched anything in the last ${context.bot.config.searchDurationText}.").queue()
            return
        }

        if (args.isEmpty()) {
            context.send().embed("Results") {
                description {
                    buildString {
                        results.forEachIndexed { index, result ->
                            append("`${index + 1}` — ${b(result.title link result.url)}").ln()
                        }
                        append("Pick one of these options from your last music searches!")
                    }
                }
            }.action().queue()
            return
        }

        val option = args[0].toIntOrNull() ?: run {
            context.send().error("The option number was not an integer.").queue()
            return
        }

        if (option !in 1..results.size) {
            context.send().error("There are only 3 search results. Pick from `1` to `3`.").queue()
            return
        }

        if (botChannel == null) {
            context.guildData.musicManager.openAudioConnection(userChannel, context)
        }

        manager.loadAndPlay(context, results[option - 1].url)
    }
}
