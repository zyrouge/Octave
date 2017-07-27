package xyz.gnarbot.gnar.commands.template.rewrite;

import java.util.LinkedHashMap;
import java.util.Map;

public class StringCursor implements Cursor {
    private final Map<String, Cursor> cursors = new LinkedHashMap<>();

    @Override
    public Map<String, Cursor> getCursors() {
        return cursors;
    }
}
