package xyz.avarel.core;

import java.util.List;

/**
 * Abstract command executor class.
 *
 * @param <C> Context argument type.
 */
public abstract class CommandExecutor<C extends DispatcherContext> {
    private final CommandInfo commandInfo;

    public CommandExecutor() {
        Command annotation = getClass().getAnnotation(Command.class);
        if (annotation == null) {
            throw new IllegalArgumentException(getClass() + "is not annotated with @Command annotation");
        }
        this.commandInfo = new CommandInfo(getClass().getAnnotation(Command.class));
    }

    public CommandExecutor(String[] aliases, String description, String usage) {
        this(new CommandInfo(aliases, description, usage));
    }

    public CommandExecutor(CommandInfo commandInfo) {
        this.commandInfo = commandInfo;
    }

    /**
     * The {@link CommandInfo command info} for the executor.
     *
     * @return Command info.
     */
    public CommandInfo getInfo() {
        return commandInfo;
    }

    /**
     * Abstract method to be executed when the command is called.
     *
     * @param context Context argument.
     * @param label Alias used to invoke the command.
     * @param args Arguments passed into the execution.
     */
    public abstract void execute(C context, String label, List<String> args);
}
