package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.IMentionable
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.entities.TextChannel
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.managed.CommandTemplate
import xyz.gnarbot.gnar.commands.managed.Executor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("ignore"),
        usage = "(user|channel|role|list) [?entity]",
        description = "Make the bot ignore certain users, channels or roles.",
        category = Category.MODERATION,
        permissions = arrayOf(Permission.ADMINISTRATOR)
)
class IgnoreCommand : CommandTemplate() {
    @Executor(0, description = "Ignore/unignore users.")
    fun user(context: Context, member: Member) {
        if (!context.member.canInteract(member)) {
            context.send().error("You can not interact with this user.").queue()
            return
        }

        context.guildOptions.ignoredUsers.let {
            if (it.contains(member.user.id)) {
                it.remove(member.user.id)

                context.send().embed("Ignore") {
                    desc {
                        "No longer ignoring user ${member.asMention}."
                    }
                }.action().queue()
            } else {
                it.add(member.user.id)

                context.send().embed("Ignore") {
                    desc {
                        "Ignored user ${member.asMention}."
                    }
                }.action().queue()
            }
        }
        context.guildOptions.save()
    }

    @Executor(1, description = "Ignore/unignore a channel.")
    fun channel(context: Context, channel: TextChannel) {
        context.guildOptions.ignoredChannels.let {
            if (it.contains(channel.id)) {
                it.remove(channel.id)

                context.send().embed("Ignore") {
                    desc {
                        "No longer ignoring channel ${channel.asMention}."
                    }
                }.action().queue()
            } else {
                it.add(channel.id)

                context.send().embed("Ignore") {
                    desc {
                        "Ignored channel ${channel.asMention}."
                    }
                }.action().queue()
            }
        }
        context.guildOptions.save()
    }

    @Executor(2, description = "Ignore/unignore a role.")
    fun role(context: Context, role: Role) {
        if (role == context.guild.publicRole) {
            context.send().error("You can't ignore the public role!").queue()
            return
        }

        context.guildOptions.ignoredRoles.let {
            if (it.contains(role.id)) {
                it.remove(role.id)

                context.send().embed("Ignore") {
                    desc {
                        "No longer ignoring role ${role.asMention}."
                    }
                }.action().queue()
            } else {
                it.add(role.id)

                context.send().embed("Ignore") {
                    desc {
                        "Ignored role ${role.asMention}."
                    }
                }.action().queue()
            }
        }
        context.guildOptions.save()
    }

    @Executor(3, description = "List ignored entities.")
    fun list(context: Context) {
        context.send().embed("Ignored Entities") {
            field("Users") {
                buildString {
                    context.guildOptions.ignoredUsers.let {
                        if (it.isEmpty()) {
                            append("None of the users are ignored.")
                        }

                        it.map(context.guild::getMemberById)
                                .filterNotNull()
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).ln() }
                    }
                }
            }
            field("Channel") {
                buildString {
                    context.guildOptions.ignoredChannels.let {
                        if (it.isEmpty()) {
                            append("None of the channels are ignored.")
                        }

                        it.map(context.guild::getTextChannelById)
                                .filterNotNull()
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).ln() }
                    }
                }
            }
            field("Roles") {
                buildString {
                    context.guildOptions.ignoredRoles.let {
                        if (it.isEmpty()) {
                            append("None of the roles are ignored.")
                        }

                        it.map(context.guild::getRoleById)
                                .filterNotNull()
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).ln() }
                    }
                }
            }
        }.action().queue()
    }
}