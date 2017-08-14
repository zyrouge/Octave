package xyz.gnarbot.gnar.commands;

public interface TCommandExecutor<T> {
    /**
     * Abstract method to be executed when the command is called.
     *
     * @param context Context argument.
     * @param label Alias used to invoke the command.
     * @param args Arguments passed into the execution.
     */
    void execute(T context, String label, String[] args);
}
