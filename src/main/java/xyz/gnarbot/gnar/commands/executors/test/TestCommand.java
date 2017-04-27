package xyz.gnarbot.gnar.commands.executors.test;

import net.dv8tion.jda.core.Permission;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        aliases = "wow",
        administrator = true,
        category = Category.NONE,
        permissions = Permission.ADMINISTRATOR
)
public class TestCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        context.send().text("wow").queue();
    }
}
