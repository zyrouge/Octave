package xyz.gnarbot.gnar.commands.executors.general;

import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.KEmbedBuilder;

@Command(aliases = "ping",
        description = "Show the bot's current response time.")
public class PingCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        long time = System.currentTimeMillis();

        context.send().embed("Ping")
                .setColor(context.getBot().getConfig().getAccentColor())
                .setDescription("Checking ping...")
                .action().queue(msg -> msg.editMessage(
                        new KEmbedBuilder().setTitle("Ping")
                                .setColor(context.getBot().getConfig().getAccentColor())
                                //.field("Receive Time", true, () -> receiveTime + " ms")
                                .field("Response Time", true, () -> {
                                    long ping = System.currentTimeMillis() - time;
                                    return ping + " ms";
                                })
                                .field("Discord API", true, () -> context.getShard().getPing() + " ms")
                                .build())
                .queue());
    }
}
