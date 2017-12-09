package xyz.avarel.core.trees.parsers;

import xyz.avarel.core.DispatcherContext;

import java.util.function.BiFunction;

public class Parser<C extends DispatcherContext, T> {
    private final String name;
    private final String description;
    private final BiFunction<C, String, T> transformer;

    public Parser(String name, String description, Parser<C, T> delegate) {
        this(name, description, delegate.transformer);
    }

    public Parser(String name, String description, BiFunction<C, String, T> transformer) {
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

    public T parse(C c, String s) {
        return transformer.apply(c, s);
    }
}