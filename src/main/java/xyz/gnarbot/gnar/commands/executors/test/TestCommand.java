package xyz.gnarbot.gnar.commands.executors.test;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Icon;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Command(
        aliases = "wow",
        administrator = true,
        category = Category.NONE,
        permissions = Permission.ADMINISTRATOR
)
public class TestCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (context.getConfig().getAvatar() != null) {
            try (InputStream is = new URL(context.getConfig().getAvatar()).openStream()) {
                context.getShard().getSelfUser().getManager().setAvatar(Icon.from(is)).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
