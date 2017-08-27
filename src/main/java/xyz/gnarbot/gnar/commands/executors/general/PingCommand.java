package xyz.gnarbot.gnar.commands.executors.general;

import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        id = 18,
        aliases = "ping",
        description = "Show the bot's current response time."
)
public class PingCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        long time = System.currentTimeMillis();

        context.getChannel().sendTyping().queue(ignored -> {
            long ping = System.currentTimeMillis() - time;
            context.getChannel().sendMessage("**REST**: " + ping + " ms\n"
                    + "**Websocket**: " + context.getJDA().getPing() + " ms").queue();
        });
    }
}
