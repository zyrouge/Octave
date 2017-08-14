package xyz.gnarbot.gnar.commands;

import xyz.gnarbot.gnar.utils.Context;

/**
 * Abstract class that is extended when creating a command.
 */
public abstract class CommandExecutor implements TCommandExecutor<Context> {
    private final Command commandInfo = this.getClass().getAnnotation(Command.class);

    /**
     * {@link Command} annotation for the executor.
     *
     * @return Command annotation for the executor.
     */
    public Command getInfo() {
        return commandInfo;
    }
}