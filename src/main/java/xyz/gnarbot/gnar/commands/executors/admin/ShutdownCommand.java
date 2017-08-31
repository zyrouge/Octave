package xyz.gnarbot.gnar.commands.executors.admin;

import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.DiscordLogBack;

@Command(
        id = 2,
        aliases = "shutdown",
        description = "Shutdown the bot.",
        admin = true,
        category = Category.NONE
)
public class ShutdownCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        try {
            Bot.getPlayers().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Shard s : Bot.getShards()) {
            s.getJda().shutdown();
        }
        DiscordLogBack.disable();

        System.exit(21);
    }
}
