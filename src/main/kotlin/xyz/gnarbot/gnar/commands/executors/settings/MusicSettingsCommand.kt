package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Channel
import net.dv8tion.jda.core.entities.VoiceChannel
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 55,
        aliases = arrayOf("musicsettings", "musicconfig"),
        description = "Change music settings.",
        category = Category.SETTINGS,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class MusicSettingsCommand : CommandTemplate() {
    @Description("Toggle music announcement.")
    fun toggle_announcements(context: Context) {
        val value = context.data.music.announce
        context.data.music.announce = !value
        context.data.save()

        if (value) {
            context.send().embed("Music Settings") {
                desc { "Announcements for music disabled."
               }
            }.action().queue()
        } else {
            context.send().embed("Music Settings") {
                desc { "Announcements for music enabled." }
            }.action().queue()
        }
    }

    @Description("Add voice channels that Gnar can play music in.")
    fun voice_channel_add(context: Context, channel: VoiceChannel) {
        if (channel.id in context.data.music.channels) {
            context.send().error("`${channel.name}` is already a music channel.").queue()
            return
        }

        if (channel == context.guild.afkChannel) {
            context.send().error("`${channel.name}` is the AFK channel, you can't play music there.").queue()
            return
        }

        context.data.music.channels.add(channel.id)
        context.data.save()

        context.send().embed("Music Settings") {
            desc { "`${channel.name}` is now a designated music channel." }
        }.action().queue()
    }

    @Description("Remove voice channels that Gnar can play music in.")
    fun voice_channel_remove(context: Context, channel: VoiceChannel) {
        if (channel.id !in context.data.music.channels) {
            context.send().error("`${channel.name}` is not one of the music channels.").queue()
            return
        }

        context.data.music.channels.remove(channel.id)
        context.data.save()

        context.send().embed("Music Settings") {
            desc { "${channel.name} is no longer a designated music channel." }
        }.action().queue()
    }

    @Description("List all settings, their description and their values.")
    fun list(context: Context) {
        context.send().embed("Music Settings") {
            field("Announcements") {
                buildString {
                    append("Music announcements are __")
                    append(if (context.data.music.announce) "enabled" else "disabled")
                    append("__, toggle with `_music toggle announcements`.")
                }
            }

            field("Music Channels") {
                buildString {
                    append("If this is not empty, Gnar will only play music in these voice channels.\n\n")
                    context.data.music.channels.let {
                        if (it.isEmpty()) append("None.")

                        it.mapNotNull(context.guild::getVoiceChannelById)
                                .map(Channel::getName)
                                .forEach { append("• ").append(it).append('\n') }
                    }
                }
            }
        }.action().queue()
    }
}
