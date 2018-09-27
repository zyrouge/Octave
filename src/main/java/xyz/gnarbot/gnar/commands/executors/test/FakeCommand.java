package xyz.gnarbot.gnar.commands.executors.test;

import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;

@Command(
        aliases = "fake",
        description = "you should hide this command"
)
@BotInfo(
        id = 9999999
)
public class FakeCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {

        context.getTextChannel().sendTyping().queue(ignored -> context.send().embed()
                .field("why", true, "you shouldt be seeing this")
                .field("this is so bleeding", true, "i want to stop')
                .action().queue());
    }
}
