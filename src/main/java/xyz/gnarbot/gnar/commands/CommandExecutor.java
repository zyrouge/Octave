package xyz.gnarbot.gnar.commands;

/**
 * Abstract class that is extended when creating a command.
 */
public abstract class CommandExecutor implements ICommandExecutor<Context> {
    private final BotInfo botInfo = this.getClass().getAnnotation(BotInfo.class);
    private final Command commandInfo = this.getClass().getAnnotation(Command.class);

    public Command getInfo() {
        return commandInfo;
    }

    public BotInfo getBotInfo() {
        return botInfo;
    }
}