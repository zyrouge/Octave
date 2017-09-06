package xyz.gnarbot.gnar.commands.template;

import xyz.gnarbot.gnar.utils.Context;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class Parser<T> {
    private final String name;
    private final String description;
    private final BiFunction<Context, String, T> transformer;

    public Parser(String name, String description, BiFunction<Context, String, T> transformer) {
        this.name = name;
        this.description = description;
        this.transformer = transformer;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Nullable
    public T parse(Context c, String s) {
        return transformer.apply(c, s);
    }
}