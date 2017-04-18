package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import xyz.gnarbot.gnar.BotConfig;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Command(aliases = "ping",
        description = "Show the bot's current response time.")
public class PingCommand extends CommandExecutor {
    @Override
    public void execute(Message message, String[] args) {
        OffsetDateTime sentTime = message.getCreationTime();

        message.respond().embed("Ping")
                .setColor(BotConfig.COLOR)
                .setDescription("Checking ping...")
                .rest().queue(msg -> msg.editMessage(
                        new EmbedBuilder().setTitle("Ping")
                                .setColor(BotConfig.COLOR)
                                //.field("Receive Time", true, () -> receiveTime + " ms")
                                .field("Response Time", true, () -> {
                                    long ping = Math.abs(sentTime.until(msg.getCreationTime(), ChronoUnit.MILLIS));
                                    return ping + " ms";
                                })
                                .field("Discord API", true, () -> getShard().getPing() + " ms")
                                .build())
                .queue());
    }
}
