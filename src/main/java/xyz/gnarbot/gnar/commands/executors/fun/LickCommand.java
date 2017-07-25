package xyz.gnarbot.gnar.commands.executors.fun;

import net.dv8tion.jda.core.entities.Member;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.template.Parser;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

@Command(
        id = 85,
        aliases = {"lick"},
        usage = "(mention|name)",
        description = "Lick people for no reason at all.",
        category = Category.FUN
)
public class LickCommand extends CommandExecutor{

    @Override
    public void execute(Context context, String[] args) {
        Member m = args.length > 0 ? Parser.MEMBER.parse(context, args[0]) : null;
        context.send().embed()
                .setDescription(m != null ? m.getEffectiveName() + ", " + context.getMessage().getAuthor().getName() + " licked you." :
                        "Lickity split, free lick.")
                .setImage(Utils.getRamMoeImage("lick"))
                .action().queue();
    }

}
