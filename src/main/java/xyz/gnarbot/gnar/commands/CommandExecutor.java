package xyz.gnarbot.gnar.commands;

import xyz.gnarbot.gnar.utils.Context;

/**
 * Abstract class that is extended when creating a command.
 */
public abstract class CommandExecutor {
    private final Command commandInfo = this.getClass().getAnnotation(Command.class);

    /**
     * {@link Command} annotation for the executor.
     *
     * @return Command annotation for the executor.
     */
    public Command getInfo() {
        return commandInfo;
    }

    /**
     * Abstract method to be executed when the command is called.
     *
     * @param context Context argument.
     * @param args Arguments passed into the execution.
     */
    protected abstract void execute(Context context, String[] args);
}