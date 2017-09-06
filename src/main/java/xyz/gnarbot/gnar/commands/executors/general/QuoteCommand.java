package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        id = 19,
        aliases = {"quote", "quotemsg"},
        usage = "(message id) [#channel]",
        description = "Quote somebody else.."
)
public class QuoteCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            Bot.getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

        TextChannel targetChannel = context.getMessage().getTextChannel();
        if (context.getMessage().getMentionedChannels().size() > 0) {
            targetChannel = context.getMessage().getMentionedChannels().get(0);
        }

        for (String id : args) {
            final TextChannel _targetChannel = targetChannel;
            context.getMessage().getChannel().getMessageById(id).queue(msg ->
                    _targetChannel.sendMessage(new EmbedBuilder()
                            .setAuthor(msg.getAuthor().getName(), null, msg.getAuthor().getAvatarUrl())
                            .setDescription(msg.getContent())
                            .build()
                    ).queue(), t -> context.send().error("Invalid message ID `" + id + "`.").queue());
        }

        context.send().info("Sent quotes to the " + targetChannel.getName() + " channel!").queue();
    }
}



