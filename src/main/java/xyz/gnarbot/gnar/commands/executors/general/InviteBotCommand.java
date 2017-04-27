package xyz.gnarbot.gnar.commands.executors.general;

import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(aliases = {"invite", "invitebot"}, description = "Get a link to invite GN4R to your server.")
public class InviteBotCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        String link = "https://discordapp.com/oauth2/authorize?client_id=201492375653056512&scope=bot&permissions=8";

        context.send().embed("Get Gnar on your server!")
                .setColor(BotConfiguration.ACCENT_COLOR)
                .setDescription("__**[Click to invite Gnar to your server.](" + link + ")**__")
                .action().queue();
    }
}
