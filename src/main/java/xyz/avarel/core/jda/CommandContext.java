package xyz.avarel.core.jda;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import xyz.avarel.core.CommandDispatcher;
import xyz.avarel.core.DispatcherContext;

public class CommandContext implements DispatcherContext {
    private final CommandDispatcher<CommandContext> dispatcher;
    private final MessageReceivedEvent event;

    public CommandContext(CommandDispatcher<CommandContext> dispatcher, MessageReceivedEvent event) {
        this.dispatcher = dispatcher;
        this.event = event;
    }

    @Override
    public String getContent() {
        return event.getMessage().getRawContent();
    }

    @Override
    public CommandDispatcher<CommandContext> getCommandDispatcher() {
        return dispatcher;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public Member getMember() {
        return event.getMember();
    }

    public Member getSelfMember() {
        return event.getGuild().getSelfMember();
    }

    public VoiceChannel getVoiceChannel() {
        return getMember().getVoiceState().getChannel();
    }

    public User getUser() {
        return event.getAuthor();
    }
}
