package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.core.entities.TextChannel;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.ResponseBuilder;

import java.util.concurrent.TimeUnit;

@Command(aliases = {"quote", "quotemsg"},
        usage = "(message id) [#channel]",
        description = "Quote somebody else..")
public class QuoteCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length == 0) {
            context.send().error("Provide a message id.").queue();
            return;
        }

        TextChannel targetChannel = context.getMessage().getTextChannel();
        if (context.getMessage().getMentionedChannels().size() > 0) {
            targetChannel = context.getMessage().getMentionedChannels().get(0);
        }

        for (String id : args) {
            if (!id.contains("#")) {
                try {
                    final TextChannel _targetChannel = targetChannel;
                    context.getMessage().getChannel().getMessageById(id).queue(msg -> new ResponseBuilder(_targetChannel).embed()
                            .setColor(BotConfiguration.ACCENT_COLOR)
                            .setAuthor(msg.getAuthor().getName(), null, msg.getAuthor().getAvatarUrl())
                            .setDescription(msg.getContent())
                            .action().queue());

                } catch (Exception e) {
                    try {
                        context.send()
                                .error("Could not find a message with the ID " + id + " within this channel.")
                                .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
                    } catch (Exception ignore) {}
                }
            }
        }

        context.send().info("Sent quotes to the " + targetChannel.getName() + " channel!")
                .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }
}



