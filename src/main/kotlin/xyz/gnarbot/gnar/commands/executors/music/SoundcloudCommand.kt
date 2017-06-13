package xyz.gnarbot.gnar.commands.executors.music

import com.jagrosh.jdautilities.menu.SelectorBuilder
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.*
import java.awt.Color

@Command(
        aliases = arrayOf("soundcloud", "sc"),
        usage = "(query...)",
        description = "Search and see YouTube results.",
        donor = true,
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class SoundcloudCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Input a query to search Soundcloud.").queue()
            return
        }

        val query = args.joinToString(" ")

        MusicManager.search("scsearch:$query", 5) { results ->
            if (results.isEmpty()) {
                context.send().error("No search results for `$query`.").queue()
                return@search
            }

            val manager = context.guildData.musicManager

            val botChannel = context.guild.selfMember.voiceState.channel
            val userChannel = context.guild.getMember(context.message.author).voiceState.channel

            if (!context.config.musicEnabled || userChannel == null || botChannel != null && botChannel != userChannel) {
                context.send().embed {
                    setAuthor("SoundCloud Results", "https://soundcloud.com", "https://soundcloud.com/favicon.ico")
                    setThumbnail("https://gnarbot.xyz/assets/img/soundcloud.png")
                    setColor(Color(255, 110, 0))

                    description {
                        buildString {
                            for (result in results) {

                                val title = result.info.title
                                val url = result.info.uri
                                val length = Utils.getTimestamp(result.duration)
                                val author = result.info.author

                                append(b(title link url)).ln()
                                append("**`").append(length).append("`** by **").append(author).append("**").ln()
                            }
                        }
                    }

                    setFooter("Want to play one of these music tracks? Join a voice channel and reenter this command.", null)
                }.action().queue()
                return@search
            } else {
                SelectorBuilder(context.bot.waiter).apply {
                    setTitle("SoundCloud Results")
                    setDescription("Select one of the following options to play them in your current music channel.")
                    setColor(Color(255, 110, 0))

                    setUser(context.user)

                    for (result in results) {
                        addOption("`${Utils.getTimestamp(result.info.length)}` ${b(result.info.title link result.info.uri)}") {
                            manager.loadAndPlay(context, result.info.uri)
                        }
                    }
                }.build().display(context.channel)
            }
        }
    }
}



