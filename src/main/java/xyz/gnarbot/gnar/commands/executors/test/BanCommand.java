package xyz.gnarbot.gnar.commands.executors.test;

import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;

@Command(
        aliases = "ban",
        description = "i can ban a deer"
)
@BotInfo(
        id = 909320923
)
public class BanCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        context.getGuild().ban(args[0], 7, args[1]).queue();
    }
}
