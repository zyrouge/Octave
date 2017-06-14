package xyz.gnarbot.gnar.commands.executors.test;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.awt.*;
import java.util.Random;

@Command(aliases = "embedthis",
        usage = "(argument)",
        category = Category.NONE,
        admin = true)
public class TestEmbedCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        String s = StringUtils.join(args, " ");
        Random r = new Random();
        String[] parts = s.split(":newsection:");
        eb.setTitle("Message from " + context.getMessage().getAuthor().getName(), null);
        eb.setDescription(parts[0]);
        parts[0] = "";
        if (parts.length > 1) {
            int id = 0;
            for (String p : parts) {
                if ("".equals(p)) {
                    id++;
                    eb.addField("Section " + id, p, false);
                }
            }
        }
        eb.setThumbnail(context.getMessage().getAuthor().getAvatarUrl())
                .setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
        MessageEmbed embed = eb.build();
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(embed);
        Message m = mb.build();
        context.getMessage().getChannel().sendMessage(m).queue();
    }
}
