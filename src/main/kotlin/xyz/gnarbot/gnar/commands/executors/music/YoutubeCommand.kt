package xyz.gnarbot.gnar.commands.executors.music

import com.jagrosh.jdautilities.menu.SelectorBuilder
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.*
import java.awt.Color

@Command(
        aliases = arrayOf("youtube", "yt"),
        usage = "(query...)",
        description = "Search and see YouTube results.",
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class YoutubeCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Input a query to search YouTube.").queue()
            return
        }

        val query = args.joinToString(" ")

        val results = try {
            YouTube.search(query, 3)
        } catch (e: RuntimeException) {
            context.send().error("Error while searching for `$query`.").queue()
            return
        }

        if (results.isEmpty()) {
            context.send().error("No search results for `$query`.").queue()
            return
        }

        val manager = context.guildData.musicManager

        val botChannel = context.guild.selfMember.voiceState.channel
        val userChannel = context.guild.getMember(context.message.author).voiceState.channel

        if (userChannel == null || botChannel != null && botChannel != userChannel) {
            context.send().embed {
                setAuthor("YouTube Results", "https://www.youtube.com", "https://s.ytimg.com/yts/img/favicon_144-vflWmzoXw.png")
                setThumbnail("https://gnarbot.xyz/assets/img/youtube.png")
                setColor(Color(141, 20, 0))

                description {
                    buildString {
                        for (result in results) {

                            val title = result.title
                            val desc = result.description
                            val url = result.url

                            append(b(title link url)).ln()
                            append(desc).ln()
                        }
                    }
                }
            }.action().queue()
            return
        } else {
            SelectorBuilder(context.bot.waiter).apply {
                setTitle("YouTube Results")
                setDescription("Select one of the following options to play them in your current music channel.")
                setColor(Color(141, 20, 0))

                setUser(context.user)

                for (result in results) {
                    addOption(b(result.title link result.url)) {
                        if (botChannel == null) {
                            context.guildData.musicManager.openAudioConnection(userChannel, context)
                        }

                        manager.loadAndPlay(context, result.url)
                    }
                }
            }.build().display(context.channel)
        }
    }
}



