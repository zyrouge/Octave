package xyz.gnarbot.gnar.commands.executors.general

import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.util.concurrent.TimeUnit

@Command(
        aliases = arrayOf("remindme", "remind"),
        usage = "(duration) (time unit) (msg)"
)
class RemindMeCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.size >= 3) {
            val string = args.copyOfRange(2, args.size).joinToString(" ")

            val time = args[0].toIntOrNull() ?: kotlin.run {
                context.send().error("The time number was not an integer.").queue()
                return
            }

            val timeUnit = try {
                TimeUnit.valueOf(args[1].toUpperCase())
            } catch (e: IllegalArgumentException) {
                context.send().error("The specified time unit was invalid. \n`${TimeUnit.values().contentToString()}`").queue()
                return
            }

            if (time > 0) {
                context.send().embed("Reminder Scheduled") {
                    setDescription("I'll be reminding you in __$time ${timeUnit.toString().toLowerCase()}__.")
                }.action().queue()

                context.message.author.openPrivateChannel().queue {
                    context.send(it).embed("Reminder from $time ${timeUnit.toString().toLowerCase()} ago.") {
                        setDescription(string)
                    }.action().queueAfter(time.toLong(), timeUnit)
                }
            } else {
                context.send().error("Number must be more than 0.").queue()
            }
        } else {
            context.send().error("Insufficient amount of arguments. `(#) (unit) (msg)`").queue()
        }
    }
}
