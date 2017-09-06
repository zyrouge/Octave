package xyz.gnarbot.gnar.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.utils.response.GuildResponseBuilder;
import xyz.gnarbot.gnar.utils.response.ResponseBuilder;

public class CommandContext {
    private Message message;
    private TextChannel textChannel;
    private Guild guild;
    private JDA jda;
    private Shard shard;
    private Member member;
    private User user;

    public CommandContext(GuildMessageReceivedEvent event) {
        this.jda = event.getJDA();
        this.shard = Bot.getShard(this.jda);

        this.message = event.getMessage();
        this.user = event.getAuthor();

        this.textChannel = event.getChannel();
        this.guild = event.getGuild();
        this.member = event.getMember();
    }

    public CommandContext(PrivateMessageReceivedEvent event) {
        this.jda = event.getJDA();
        this.shard = Bot.getShard(this.jda);

        this.message = event.getMessage();
        this.user = event.getAuthor();

        this.textChannel = null;
        this.guild = null;
        this.member = null;
    }

    public Message getMessage() {
        return this.message;
    }

    public TextChannel getTextChannel() {
        return this.textChannel;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public Shard getShard() {
        return this.shard;
    }

    public Member getMember() {
        return this.member;
    }

    public Member getSelfMember() {
        return this.guild.getSelfMember();
    }

    public VoiceChannel getVoiceChannel() {
        return getMember().getVoiceState().getChannel();
    }

    public User getUser() {
        return this.user;
    }

    public ResponseBuilder send() {
        return new GuildResponseBuilder(getTextChannel());
    }
}
