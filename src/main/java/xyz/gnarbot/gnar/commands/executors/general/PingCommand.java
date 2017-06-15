package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.core.EmbedBuilder;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(aliases = "ping",
        description = "Show the bot's current response time.")
public class PingCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        long time = System.currentTimeMillis();

        context.send().embed("Ping")
                .setDescription("Checking ping...")
                .action().queue(msg -> {
            long ping = System.currentTimeMillis() - time;
            msg.editMessage(new EmbedBuilder().setTitle("Ping")
                    .addField("Response Time", ping + " ms", true)
                    .addField("Discord API", context.getShard().getPing() + " ms", true)
                    .build()).queue();
        });
    }
}
