package xyz.gnarbot.gnar.commands.fun;

import xyz.gnarbot.gnar.commands.*;

@Command(
        aliases = "8ball",
        usage = "(question)",
        description = "Ask for your wildest dreams!"
)
@BotInfo(
        id = 4,
        category = Category.FUN
)
public class EightBallCommand extends CommandExecutor {
    private final String[] responses = {
            "It is certain.", "It is decidedly so.", "Without a doubt.", "Yes, definitely.", "You may rely on it.",
            "As I see it, yes.", "Most likely.", "Outlook good.", "Yes.", "Signs point to yes.", "Reply hazy try again.",
            "Ask again later.", "Better not tell you now.", "Cannot predict now.", "Concentrate and ask again.",
            "Don't count on it.", "My reply is no.", "My sources say no.", "Outlook not so good.", "Very doubtful."
    };

    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            context.getBot().getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

        context.send().info("\uD83C\uDFB1 " + responses[(int) (Math.random() * responses.length)]).queue();
    }
}
