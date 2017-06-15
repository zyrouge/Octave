package xyz.gnarbot.gnar.commands.executors.test;

import net.dv8tion.jda.core.Permission;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

@Command(
        aliases = "wow",
        admin = true,
        category = Category.NONE,
        permissions = Permission.ADMINISTRATOR
)
public class TestCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        System.out.println(Utils.hasteBin("what"));
//        if (Bot.getConfig().getAvatar() != null) {
//            try (InputStream is = new URL(Bot.getConfig().getAvatar()).openStream()) {
//                context.getShard().getSelfUser().getManager().setAvatar(Icon.from(is)).queue();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
