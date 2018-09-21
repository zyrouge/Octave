package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["updates"],
        description = "Get all of the custom emotes the bot has access to"
)
@BotInfo(
        id = 101,
        category = Category.NONE,
        admin = true
)
class TestCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        var count = 0
        context.guild.roles.iterator().forEach { role ->

            if(role.name == "Updates") {
                context.guild.members.forEach { member ->
                    context.guild.controller.addRolesToMember(member, role).queue()
                    count++
                    Thread.sleep(2000)
                }

            }

        }
        context.textChannel.sendMessage("Added Role \"Updates\" to $count users.").queue()
    }
}