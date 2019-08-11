package xyz.gnarbot.gnar.commands.executors.test;

import net.dv8tion.jda.api.Permission;
import xyz.gnarbot.gnar.commands.*;

@Command(
        aliases = "fake",
        description = "you should hide this command"
)
@BotInfo(
        id = 9999999,
        permissions = Permission.VIEW_AUDIT_LOGS,
        category = Category.NONE
)
public class FakeCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {

        context.getTextChannel().sendTyping().queue(ignored -> context.send().embed()
                .field("why", true, "you should'nt be seeing this")
                .field("this is so bleeding", true, "i want to stop")
                .action().queue());
    }
}
