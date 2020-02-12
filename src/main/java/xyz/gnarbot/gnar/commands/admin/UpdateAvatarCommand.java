package xyz.gnarbot.gnar.commands.admin;

import net.dv8tion.jda.api.entities.Icon;
import xyz.gnarbot.gnar.commands.*;

import java.io.File;

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
    public void execute(Context context, String label, String[] args) {
        try {
            context.getJDA().getSelfUser().getManager().setAvatar(Icon.from(new File("/home/Gnar/logo.png"))).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
