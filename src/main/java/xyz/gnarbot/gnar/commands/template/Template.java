package xyz.gnarbot.gnar.commands.template;

import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.EmbedMaker;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

public interface Template {
    Map<String, Template> getCursors();

    default void add(String key, Template template) {
        if (getCursors().containsKey(key)) {
            throw new IllegalStateException("Trying to override " + key);
        }
        getCursors().put(key, template);
    }

    default String description() {
        StringJoiner sj = new StringJoiner(", ");

        for (String cursor : getCursors().keySet()) {
            sj.add(cursor);
        }

        return sj.toString();
    }

    default void execute(Context context, String[] args) {
        if (args.length != 0) {
            Template template = getCursors().get(args[0]);
            if (template != null) {
                template.execute(context, Arrays.copyOfRange(args, 1, args.length));
                return;
            }
        }
        helpMessage(context, args);
    }

    default void helpMessage(Context context, String[] args) {
        helpMessage(context, args, null, null);
    }

    default void helpMessage(Context context, String[] args, String title, String description) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Template> entry : getCursors().entrySet()) {
            builder.append("• `");
            builder.append(entry.getKey());

            Map.Entry<String, Template> current = entry;
            while (true) {
                Template template = current.getValue();
                if (template.getCursors().size() == 1) {
                    current = new ArrayList<>(template.getCursors().entrySet()).get(0);
                    builder.append(' ').append(current.getKey());
                    continue;
                } else if (template.getCursors().size() > 1) {
                    builder.append("...");
                } else if (template instanceof MethodTemplate) {
                    MethodTemplate value = (MethodTemplate) template;
                    builder.append(value.requirements());
                }
                break;
            }

            builder.append("`");
            builder.append("\n").append(current.getValue().description());
            builder.append("\n\n");
        }

        EmbedMaker eb = new EmbedMaker();
        eb.setColor(context.getGuild().getSelfMember().getColor());

        if (title == null && description == null) {
            eb.setTitle("Arguments");
            eb.setDescription(builder);
        } else if (description == null) {
            eb.setTitle(title);
            eb.field("Arguments", false, builder);
        } else {
            eb.setTitle(title);
            eb.setDescription(description);
            eb.field("Arguments", false, builder);
        }

        if (args.length != 0) {
            eb.setTitle("Invalid Argument");
            eb.setColor(Color.RED);
        }

        context.getChannel().sendMessage(eb.build()).queue();
    }
}
