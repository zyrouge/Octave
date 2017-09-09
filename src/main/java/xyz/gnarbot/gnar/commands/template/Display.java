package xyz.gnarbot.gnar.commands.template;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Display {
    String value();
}
