package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Channel
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.VoiceChannel
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.managed.CommandTemplate
import xyz.gnarbot.gnar.commands.managed.Executor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("musicconfig", "musicsettings"),
        description = "Change music settings.",
        category = Category.MODERATION,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class MusicSettingsCommand : CommandTemplate() {
    @Executor(0, description = "Set the request channel.")
    fun setRequestChannel(context: Context, channel: TextChannel) {
        if (context.guildOptions.requestChannel == channel.id) {
            context.send().error("${channel.asMention} is already the request channel.").queue()
            return
        }

        context.guildOptions.requestChannel = channel.id
        context.guildOptions.save()

        context.send().embed("Music Settings") {
            desc {
                "Music commands can now only be used in ${channel.asMention}."
            }
        }.action().queue()
    }

    @Executor(1, description = "Unset the request channel.")
    fun unsetRequestChannel(context: Context) {
        if (context.guildOptions.requestChannel == null) {
            context.send().error("The request channel is not set.").queue()
            return
        }

        context.guildOptions.requestChannel = null
        context.guildOptions.save()

        context.send().embed("Music Settings") {
            desc {
                "Music commands can now be used in any text channels."
            }
        }.action().queue()
    }

    @Executor(2, description = "Add voice channels that Gnar can play music in.")
    fun addMusicChannel(context: Context, channel: VoiceChannel) {
        if (channel.id in context.guildOptions.musicChannels) {
            context.send().error("`${channel.name}` is already a music channel.").queue()
            return
        }

        context.guildOptions.musicChannels.add(channel.id)
        context.guildOptions.save()

        context.send().embed("Music Settings") {
            desc {
                "`${channel.name}` is now a designated music channel."
            }
        }.action().queue()
    }

    @Executor(3, description = "Remove voice channels that Gnar can play music in.")
    fun removeMusicChannel(context: Context, channel: VoiceChannel) {
        if (channel.id !in context.guildOptions.musicChannels) {
            context.send().error("`${channel.name}` is not one of the music channels.").queue()
            return
        }

        context.guildOptions.musicChannels.remove(channel.id)
        context.guildOptions.save()

        context.send().embed("Music Settings") {
            desc {
                "${channel.name} is no longer a designated music channel."
            }
        }.action().queue()
    }

    @Executor(4, description = "List all settings, their description and their values.")
    fun list(context: Context) {
        context.send().embed("Music Settings") {
            field("Request Channel", false) {
                buildString {
                    append("If this channel is set, music commands will only be allowed to be used in that channel.").ln().ln()
                    append(context.guildOptions.requestChannel?.let { context.guild.getTextChannelById(it).asMention  } ?: "None")
                }
            }
            field("Channel") {
                buildString {
                    append("If this is not empty, Gnar will only play music in these voice channels.").ln().ln()
                    context.guildOptions.musicChannels.let {
                        if (it.isEmpty()) {
                            append("None.")
                        }

                        it.map(context.guild::getVoiceChannelById)
                                .filterNotNull()
                                .map(Channel::getName)
                                .forEach { append("• ").append(it).ln() }
                    }
                }
            }
        }.action().queue()
    }
}