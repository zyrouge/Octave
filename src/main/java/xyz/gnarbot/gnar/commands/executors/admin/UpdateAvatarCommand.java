package xyz.gnarbot.gnar.commands.executors.admin;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Icon;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.*;
import xyz.gnarbot.gnar.utils.DiscordLogBack;

import java.io.File;
import java.io.IOException;

@Command(
        aliases = "updateavatar",
        description = "Update Avatar of bot"
)
@BotInfo(
        id = 102,
        admin = true,
        category = Category.NONE
)
public class UpdateAvatarCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args)  {
        try {
            context.getJDA().getSelfUser().getManager().setAvatar(Icon.from(new File("/home/Gnar/logo.png"))).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
