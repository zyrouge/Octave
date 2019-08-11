package xyz.gnarbot.gnar.commands.executors.general;

import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;

@Command(
        aliases = {"shardnumber", "activeshard"},
        description = "Display your shard number"
)
@BotInfo(
        id = 103
)
public class ActiveShardCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        context.send().embed("Shard Checker")
                .description("Your current shard you are on is: " + context.getJDA().getShardInfo().getShardId())
                .action().queue();
    }
}
