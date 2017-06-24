package xyz.gnarbot.gnar.commands.executors.general;

import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(aliases = "ping",
        description = "Show the bot's current response time.")
public class PingCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        long time = System.currentTimeMillis();

        context.send().text("Checking ping...").queue(msg -> {
            long ping = System.currentTimeMillis() - time;
            context.send().text("**Response Time**: " + ping + " ms\n"
                    + "**Discord API**: " + context.getJda().getPing() + " ms").queue();
        });
    }
}
