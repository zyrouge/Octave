package xyz.gnarbot.gnar.commands.executors.admin;

import net.dv8tion.jda.core.entities.Game;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        aliases = "restartbot",
        admin = true,
        category = Category.NONE
)
public class RestartBotCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        for(Shard s : Bot.getShards()) {
            s.getPresence().setGame(Game.of("Restarting bot..."));
        }
        System.exit(21);
    }
}
