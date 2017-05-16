package xyz.gnarbot.gnar.commands.executors.admin;

import net.dv8tion.jda.core.entities.Game;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        aliases = "restartbot",
        administrator = true)
public class RestartBotCommand extends CommandExecutor {

    @Override
    public void execute(Context context, String[] args) {

        for(Shard s : context.getBot().getShards()) {
            s.getPresence().setGame(Game.of("Restarting bot..."));
        }

        System.exit(21);

    }

}
