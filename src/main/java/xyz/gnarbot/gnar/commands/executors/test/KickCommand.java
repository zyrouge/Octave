package xyz.gnarbot.gnar.commands.executors.test;

import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;

@Command(
        aliases = "kick",
        description = "i suck at everything"
)
@BotInfo(
        id = 66686868
)
public class KickCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        context.getGuild().kick(args[0], args[1]).queue();
    }
}

