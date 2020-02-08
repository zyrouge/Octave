package xyz.gnarbot.gnar.commands.executors.general;

import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;
import xyz.gnarbot.gnar.db.PremiumKey;
import xyz.gnarbot.gnar.db.Redeemer;

import java.awt.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

@Command(
        aliases = "redeem",
        usage = "(code)",
        description = "Redeem a key for your server."
)
@BotInfo(
        id = 21
)
public class RedeemCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            context.getBot().getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

        String id = args[0];

        PremiumKey key = context.getBot().db().getPremiumKey(id);

        if (key != null) {
            if (key.getRedeemer() != null) {
                context.send().error("That code has already been redeemed.").queue();
                return;
            }

            switch (key.getType()) {
                case PREMIUM:
                    key.setRedeemer(new Redeemer(Redeemer.Type.GUILD, context.getGuild().getId()));
                    System.out.println(key.getRedeemer());
                    key.save();

                    context.getData().addPremiumKey(key.getId(), key.getDuration());
                    context.getData().save();

                    OffsetDateTime expiresBy = OffsetDateTime.ofInstant(Instant.ofEpochMilli(context.getData().getPremiumUntil()), ZoneId.systemDefault());

                    context.send().embed("Premium Code")
                            .setColor(Color.ORANGE)
                            .description("Redeemed key `" + key.getId() + "`. **Thank you for supporting the bot's development!**\n")
                            .appendDescription("Your **Premium** status will be valid until `" + expiresBy.format(DateTimeFormatter.RFC_1123_DATE_TIME) + "`.")
                            .field("Donator Perks", true,  new StringJoiner("\n")
                                    .add("• `volume` Change the volume of the music player!")
                                    .add("• First access to new features.")
                                    .add("• Use the music bot during maximum music capacity.")
                            )
                            .action().queue();
                    break;
                default:
                    context.send().error("Unknown key type.").queue();
            }
        } else {
            context.send().error("That is not a valid code.").queue();
        }
    }
}