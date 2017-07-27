package xyz.gnarbot.gnar.commands.template;

import java.util.LinkedHashMap;
import java.util.Map;

public class StringTemplate implements Template {
    private final Map<String, Template> cursors = new LinkedHashMap<>();

    @Override
    public Map<String, Template> getCursors() {
        return cursors;
    }
}
