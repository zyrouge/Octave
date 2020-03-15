package xyz.gnarbot.gnar.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;

import java.time.format.DateTimeFormatter;

@Command(
        aliases = {"quote", "quotemsg"},
        usage = "(message id) [#channel]",
        description = "Quote somebody else.."
)
@BotInfo(
        id = 19
)
public class QuoteCommand extends CommandExecutor {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            context.getBot().getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

        TextChannel targetChannel = context.getMessage().getTextChannel();
        if (context.getMessage().getMentionedChannels().size() > 0) {
            targetChannel = context.getMessage().getMentionedChannels().get(0);
        }

        final TextChannel _targetChannel = targetChannel;

        for (String id : args) {
            try {
                if(id.equals(targetChannel.getAsMention()))
                    continue;

                context.getMessage().getChannel().retrieveMessageById(id).queue(msg ->
                        _targetChannel.sendMessage(new EmbedBuilder()
                                .setAuthor(msg.getAuthor().getName(), null, msg.getAuthor().getAvatarUrl())
                                .setDescription(msg.getContentDisplay())
                                .addField("Sent At", fmt.format(msg.getTimeCreated()), false)
                                .setFooter("ID: " + msg.getId())
                                .build()
                        ).queue(), t -> context.send().error("Invalid message ID `" + id + "`.").queue());
            } catch (IllegalArgumentException e) {
                context.send().error("Invalid message ID `" + id + "`.").queue();
            }
        }

        context.send().info("Sent quotes to the " + targetChannel.getName() + " channel!").queue();
    }
}



