package xyz.gnarbot.gnar.commands.executors.test;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.template.Executor;
import xyz.gnarbot.gnar.commands.template.rewrite.CommandCursor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        id = 32,
        aliases = "wow",
        admin = true,
        category = Category.NONE,
        permissions = Permission.ADMINISTRATOR
)
public class TestCommand extends CommandCursor {
    @Executor(value = 0, description = "kode is a meme")
    public void test(Context context) {
        System.out.println("meme");
    }

    @Executor(value = 1, description = "natan and his fucking asm <3")
    public void tasty_memes(Context context) {
        System.out.println("meme 2");
    }

    @Executor(value = 3, description = "adrian is bae")
    public void tasty_food(Context context) {
        System.out.println("meme 3");
    }

    @Executor(value = 3, description = "so is avarel")
    public void you_are_a_meme(Context context, Member user) {
        context.send().info(user.getAsMention()).queue();
    }
}
