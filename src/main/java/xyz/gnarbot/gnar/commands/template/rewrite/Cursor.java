package xyz.gnarbot.gnar.commands.template.rewrite;

import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.EmbedMaker;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public interface Cursor {
    Map<String, Cursor> getCursors();
    default void add(String key, Cursor cursor) {
        if (getCursors().containsKey(key)) {
            throw new IllegalStateException();
        }
        getCursors().put(key, cursor);
    }

    default String description() {
        return getCursors().keySet().toString();
    }

    default void execute(Context context, String[] args) {
        if (args.length != 0) {
            Cursor cursor = getCursors().get(args[0]);
            if (cursor != null) {
                cursor.execute(context, Arrays.copyOfRange(args, 1, args.length));
                return;
            }
        }

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Cursor> entry : getCursors().entrySet()) {
            builder.append("• `");
            builder.append(entry.getKey());

            Map.Entry<String, Cursor> current = entry;
            while (true) {
                Cursor cursor = current.getValue();
                if (cursor.getCursors().size() == 1) {
                    current = new ArrayList<>(cursor.getCursors().entrySet()).get(0);
                    builder.append(' ').append(current.getKey());
                    continue;
                } else if (cursor.getCursors().size() > 1) {
                    builder.append("...");
                } else if (cursor instanceof MethodCursor) {
                    MethodCursor value = (MethodCursor) cursor;
                    builder.append(value.requirements());
                }
                break;
            }

            builder.append("`");
            builder.append("\n").append(current.getValue().description());
            builder.append("\n\n");
        }

        EmbedMaker eb = new EmbedMaker();

        Color color = context.getGuild().getSelfMember().getColor();
        if (color == null) color = Color.WHITE;
        eb.setColor(color);

        eb.setTitle("Arguments");
        if (args.length != 0) {
            eb.setTitle("Invalid arguments.");
            eb.setColor(Color.RED);
        }
        eb.setDescription(builder.toString());

        context.getChannel().sendMessage(eb.build()).queue();
    }
}
